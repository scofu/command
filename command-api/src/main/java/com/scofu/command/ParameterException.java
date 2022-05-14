package com.scofu.command;

import com.scofu.command.model.Parameter;
import net.kyori.adventure.text.Component;

/** Parameter related exception. */
public class ParameterException extends DispatchException {

  private final Parameter<?> parameter;

  public ParameterException(Component message, Parameter<?> parameter) {
    super(message);
    this.parameter = parameter;
  }

  public ParameterException(Component message, Throwable cause, Parameter<?> parameter) {
    super(message, cause);
    this.parameter = parameter;
  }

  /** Returns the parameter. */
  public Parameter<?> parameter() {
    return parameter;
  }
}
