package com.scofu.command.validation;

import com.scofu.command.Context;
import com.scofu.command.DispatchHandleUnvalidatedException;
import com.scofu.command.model.Node;

/**
 * Validates handles.
 *
 * <p>If a handle is unvalidated on dispatch, an exception will be thrown and the context will be
 * notified through {@link Context#onDispatchResolveError(Iterable, Object, Throwable)}, where the
 * exception will be a {@link DispatchHandleUnvalidatedException}.
 */
public interface Validator {

  /**
   * Returns an empty validator that doesn't validate at all.
   */
  static Validator empty() {
    return Empty.EMPTY;
  }

  /**
   * Validates the given context for the given node.
   *
   * @param context the context
   * @param node    the node
   * @param <T>     the type of the input
   * @param <R>     the type of the output
   */
  <T, R> boolean validate(Context context, Node<T, R> node);

  /**
   * Empty validator.
   */
  class Empty implements Validator {

    private static final Empty EMPTY = new Empty();

    @Override
    public <T, R> boolean validate(Context context, Node<T, R> node) {
      return true;
    }
  }
}
