package com.scofu.command.model;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The parameterized part of a node.
 *
 * <p>Can be shared by multiple nodes.
 */
public record Handle(List<Parameter<?>> parameters) {

  /** Creates and returns a new builder. */
  public static HandleBuilder<Void> handle() {
    return new HandleBuilder<>();
  }

  /** Returns a new builder from this. */
  public HandleBuilder<Handle> toBuilder() {
    final var result = new AtomicReference<Handle>();
    return new HandleBuilder<>(this, result::get, result::set);
  }
}
