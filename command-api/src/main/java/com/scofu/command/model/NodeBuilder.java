package com.scofu.command.model;

import com.scofu.command.target.Suggester;
import com.scofu.command.target.Target;

/**
 * Builds nodes.
 *
 * <p>See {@link HandleBuilder} and {@link ParameterBuilder}.
 *
 * @param <T> the type of the input to the node
 * @param <R> the type of the output from the node
 */
@SuppressWarnings("unused")
public interface NodeBuilder<T, R> extends Expandable<NodeBuilder<T, R>> {

  /**
   * Sets the handle.
   *
   * @param handle the handle
   */
  NodeBuilder<T, R> withHandle(Handle handle);

  /** Sets the handle. */
  HandleBuilder<T, R> withHandle();

  /**
   * Sets the target.
   *
   * @param target the target
   * @param <K> the type of the input
   * @param <V> the type of the output
   */
  <K, V> NodeBuilder<K, V> withTarget(Target<K, V> target);

  /**
   * Sets the suggester.
   *
   * @param suggester the suggester
   */
  NodeBuilder<T, R> withSuggester(Suggester<T> suggester);

  /**
   * Adds a child.
   *
   * @param identifier the identifier
   * @param aliases the aliases
   * @param <K> the type of the input
   * @param <V> the type of the output
   * @param <U> the type of the child's identifier
   */
  @SuppressWarnings("unchecked")
  <K, V, U> NodeBuilder<K, V> withChild(Identifier<U> identifier, Identifier<U>... aliases);

  /** Sets the child. */
  NodeBuilder<?, ?> endChild();

  /** Builds and returns a new node. */
  Node<T, R> build();
}
