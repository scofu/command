package com.scofu.command;

import com.scofu.command.model.Handle;
import com.scofu.command.model.Node;
import net.kyori.adventure.text.Component;

/** Unvalidated handle exception. */
public class DispatchHandleUnvalidatedException extends DispatchException {

  private final Handle handle;
  private final Node<?, ?> node;

  /**
   * Constructs a new exception.
   *
   * @param message the message
   * @param handle the handle
   * @param node the node
   */
  public DispatchHandleUnvalidatedException(Component message, Handle handle, Node<?, ?> node) {
    super(message);
    this.handle = handle;
    this.node = node;
  }

  /**
   * Constructs a new exception.
   *
   * @param message the message
   * @param cause the cause
   * @param handle the handle
   * @param node the node
   */
  public DispatchHandleUnvalidatedException(
      Component message, Throwable cause, Handle handle, Node<?, ?> node) {
    super(message, cause);
    this.handle = handle;
    this.node = node;
  }

  /** Returns the handle. */
  public Handle handle() {
    return handle;
  }

  /** Returns the node. */
  public Node<?, ?> node() {
    return node;
  }
}
