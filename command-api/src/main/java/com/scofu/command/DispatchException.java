package com.scofu.command;

import static com.google.common.base.Preconditions.checkNotNull;

import net.kyori.adventure.text.Component;

/** Dispatch exception. */
public class DispatchException extends RuntimeException {

  private final Component message;

  /**
   * Constructs a new dispatch exception.
   *
   * @param message the message
   */
  public DispatchException(Component message) {
    checkNotNull(message, "message");
    this.message = message;
  }

  /**
   * Constructs a new dispatch exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public DispatchException(Component message, Throwable cause) {
    super(cause);
    checkNotNull(message, "message");
    checkNotNull(cause, "cause");
    this.message = message;
  }

  /** Returns the message. */
  public Component message() {
    return message;
  }
}
