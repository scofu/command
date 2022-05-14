package com.scofu.command.target;

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;

/**
 * A target that invokes a method.
 *
 * @param <R> the type of the output
 */
public class MethodTarget<R> implements Target<Object[], R> {

  private final Method method;
  private final Object instance;

  public MethodTarget(Method method, Object instance) {
    checkNotNull(method, "method");
    this.method = method;
    this.instance = instance;
  }

  @SuppressWarnings("unchecked")
  @Override
  public R invoke(Command command, Object... argument) throws Throwable {
    final var result = method.invoke(instance, argument);
    if (result == null) {
      return null;
    }
    return (R) result;
  }
}
