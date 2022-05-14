package com.scofu.command;

import net.kyori.adventure.text.Component;

/** Dispatch exception. */
public class DispatchException extends RuntimeException {

  private final Component message;

  public DispatchException(Component message) {
    this.message = message;
  }

  public DispatchException(Component message, Throwable cause) {
    super(cause);
    this.message = message;
  }

  /** Returns the message. */
  public Component message() {
    return message;
  }
}
