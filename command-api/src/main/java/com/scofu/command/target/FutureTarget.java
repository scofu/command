package com.scofu.command.target;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import com.scofu.command.model.Node;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * A completable future (async) target.
 *
 * @param <T> the type of the input
 * @param <R> the type of the output
 */
public class FutureTarget<T, R> implements Target<T, CompletableFuture<R>> {

  private final Executor executor;
  private final Target<T, R> target;

  /**
   * Constructs a new future target.
   *
   * @param executor the executor
   * @param target  the target
   */
  public FutureTarget(Executor executor, Target<T, R> target) {
    checkNotNull(executor, "executor");
    checkNotNull(target, "target");
    this.executor = executor;
    this.target = target;
  }

  @SuppressWarnings("unchecked")
  @Override
  public CompletableFuture<R> invoke(Command command, T argument) throws Throwable {
    checkNotNull(command, "command");
    return supplyAsync(() -> {
      try {
        return target.invoke(command, argument);
      } catch (Throwable e) {
        throw new ForwardedException(e);
      }
    }, executor).whenComplete(((r, throwable) -> {
      if (throwable instanceof CompletionException completionException
          && completionException.getCause() instanceof ForwardedException forwardedException) {
        command.context()
            .onDispatchInvokeError(command.identifiers(), (Node<T, R>) command.node(), argument,
                forwardedException.getCause());
      }
    }));
  }

  private static class ForwardedException extends RuntimeException {

    public ForwardedException(Throwable cause) {
      super(cause);
    }
  }
}
