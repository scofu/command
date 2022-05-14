package com.scofu.command;

import static com.google.common.base.Preconditions.checkNotNull;

import com.scofu.command.model.Handle;
import com.scofu.command.model.Node;
import java.util.Optional;
import javax.annotation.Nullable;
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
  public DispatchHandleUnvalidatedException(
      Component message, @Nullable Handle handle, Node<?, ?> node) {
    super(message);
    checkNotNull(message, "message");
    checkNotNull(node, "node");
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
      Component message, Throwable cause, @Nullable Handle handle, Node<?, ?> node) {
    super(message, cause);
    checkNotNull(message, "message");
    checkNotNull(cause, "cause");
    checkNotNull(node, "node");
    this.handle = handle;
    this.node = node;
  }

  /** Returns the optional handle. */
  public Optional<Handle> handle() {
    return Optional.ofNullable(handle);
  }

  /** Returns the node. */
  public Node<?, ?> node() {
    return node;
  }
}
