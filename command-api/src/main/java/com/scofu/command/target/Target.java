package com.scofu.command.target;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Consumer;

/**
 * Represents the target function of a node.
 *
 * @param <T> the type of the input
 * @param <R> the type of the output
 */
public interface Target<T, R> {

  R invoke(Command command, T argument) throws Throwable;

  /**
   * Returns a target that first invokes this and then invokes the given target with the result of
   * this' invocation.
   *
   * <p>Can be used to change output.
   *
   * @param target the target
   * @param <V> the type of the new output
   */
  default <V> Target<T, V> then(Target<R, V> target) {
    checkNotNull(target, "target");
    return (command, argument) -> target.invoke(command, invoke(command, argument));
  }

  /**
   * Returns a target that first invokes this and then accepts the given target with the result of
   * this' invocation.
   *
   * @param consumer the consumer
   */
  default Target<T, R> then(Consumer<R> consumer) {
    checkNotNull(consumer, "consumer");
    return then(
        (command, argument) -> {
          consumer.accept(argument);
          return argument;
        });
  }

  /**
   * Returns a target that first invokes this target with the result of the invocation of the given
   * target.
   *
   * <p>Can be used to change input.
   *
   * @param target the target
   * @param <V> the type of the new input
   */
  default <V> Target<V, R> compose(Target<V, T> target) {
    checkNotNull(target, "target");
    return (command, argument) -> invoke(command, target.invoke(command, argument));
  }
}
