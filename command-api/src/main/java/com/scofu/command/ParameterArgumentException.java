package com.scofu.command;

import static com.google.common.base.Preconditions.checkNotNull;

import com.scofu.command.model.Parameter;
import net.kyori.adventure.text.Component;

/** Parameter argument exception. */
public class ParameterArgumentException extends ParameterException {

  public ParameterArgumentException(Component message, Parameter<?> parameter) {
    super(message, parameter);
    checkNotNull(message, "message");
    checkNotNull(parameter, "parameter");
  }

  public ParameterArgumentException(Component message, Throwable cause, Parameter<?> parameter) {
    super(message, cause, parameter);
    checkNotNull(message, "message");
    checkNotNull(cause, "cause");
    checkNotNull(parameter, "parameter");
  }
}
