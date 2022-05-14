package com.scofu.command;

import static com.google.common.base.Preconditions.checkNotNull;

import com.scofu.command.model.Parameter;
import net.kyori.adventure.text.Component;

/** Parameter related exception. */
public class ParameterException extends DispatchException {

  private final Parameter<?> parameter;

  /**
   * Constructs a new parameter exception.
   *
   * @param message the message
   * @param parameter the parameter
   */
  public ParameterException(Component message, Parameter<?> parameter) {
    super(message);
    checkNotNull(message, "message");
    checkNotNull(parameter, "parameter");
    this.parameter = parameter;
  }

  /**
   * Constructs a new parameter exception.
   *
   * @param message the message
   * @param cause the cause
   * @param parameter the parameter
   */
  public ParameterException(Component message, Throwable cause, Parameter<?> parameter) {
    super(message, cause);
    checkNotNull(message, "message");
    checkNotNull(cause, "cause");
    checkNotNull(parameter, "parameter");
    this.parameter = parameter;
  }

  /** Returns the parameter. */
  public Parameter<?> parameter() {
    return parameter;
  }
}
