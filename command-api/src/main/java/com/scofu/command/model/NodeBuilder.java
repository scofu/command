package com.scofu.command.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.scofu.command.target.Suggester;
import com.scofu.command.target.Target;
import com.scofu.common.AbstractBuilder;
import com.scofu.common.Expandable;
import com.scofu.common.ExpansionMap;
import com.scofu.common.Identifier;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/**
 * Builds nodes.
 *
 * @param <T> the type of the input
 * @param <R> the type of the output
 * @param <S> the type of the parent
 */
public class NodeBuilder<T, R, S> extends AbstractBuilder<Node<T, R>, S, NodeBuilder<T, R, S>>
    implements Expandable<NodeBuilder<T, R, S>> {

  private final ExpansionMap expansions;
  private final Map<Identifier<?>, Node<?, ?>> nodes;
  private final Map<Identifier<?>, Node<?, ?>> aliasedNodes;
  private List<Identifier<?>> identifiers;
  private Handle handle;
  private Target<T, R> target;
  private Suggester<T> suggester;

  /**
   * Constructs a new node builder.
   *
   * @param from the from
   * @param parent the parent
   * @param consumer the consumer
   */
  public NodeBuilder(
      @Nullable Node<T, R> from,
      @Nullable Supplier<S> parent,
      @Nullable Consumer<Node<T, R>> consumer) {
    super(from, parent, consumer);
    this.expansions = ExpansionMap.expansionMap();
    this.nodes = Maps.newConcurrentMap();
    this.aliasedNodes = Maps.newConcurrentMap();
  }

  /**
   * Constructs a new node builder.
   *
   * @param parent the parent
   * @param consumer the consumer
   */
  public NodeBuilder(@Nullable Supplier<S> parent, @Nullable Consumer<Node<T, R>> consumer) {
    super(parent, consumer);
    this.expansions = ExpansionMap.expansionMap();
    this.nodes = Maps.newConcurrentMap();
    this.aliasedNodes = Maps.newConcurrentMap();
  }

  /**
   * Constructs a new node builder.
   *
   * @param from the from
   */
  public NodeBuilder(@Nullable Node<T, R> from) {
    super(from);
    this.expansions = ExpansionMap.expansionMap();
    this.nodes = Maps.newConcurrentMap();
    this.aliasedNodes = Maps.newConcurrentMap();
  }

  /** Constructs a new node builder. */
  public NodeBuilder() {
    this.expansions = ExpansionMap.expansionMap();
    this.nodes = Maps.newConcurrentMap();
    this.aliasedNodes = Maps.newConcurrentMap();
  }

  @Override
  protected void initializeFrom(Node<T, R> node) {
    expansions.putAll(node.expansions());
    identifiers = Lists.newArrayList(node.identifiers());
    handle = node.handle();
    target = node.target();
    suggester = node.suggester();
    nodes.putAll(node.nodes());
    aliasedNodes.putAll(node.aliasedNodes());
  }

  @Override
  public Node<T, R> build() {
    return new Node<>(
        require(identifiers, "identifiers"),
        handle,
        target,
        suggester,
        nodes,
        aliasedNodes,
        expansions);
  }

  @Override
  public ExpansionMap expansions() {
    return expansions;
  }

  /**
   * Adds the given value as an identifier.
   *
   * <p>See {@link Identifier#identifier(Object)}.
   *
   * @param value the value
   */
  public <V> NodeBuilder<T, R, S> identifier(V value) {
    if (identifiers == null) {
      identifiers = Lists.newArrayList();
    }
    identifiers.add(Identifier.identifier(value));
    return this;
  }

  /**
   * Sets the identifiers.
   *
   * @param identifiers the identifiers
   */
  public NodeBuilder<T, R, S> identifiers(List<Identifier<?>> identifiers) {
    this.identifiers = identifiers;
    return this;
  }

  /**
   * Sets the handle.
   *
   * @param handle the handle
   */
  public NodeBuilder<T, R, S> handle(Handle handle) {
    this.handle = handle;
    return this;
  }

  /** Builds the handle. */
  public HandleBuilder<NodeBuilder<T, R, S>> handle() {
    return new HandleBuilder<>(() -> this, handle -> this.handle = handle);
  }

  /**
   * Sets the target.
   *
   * @param target the target
   * @param <X> the type of the new input
   * @param <Y> the type of the new output
   */
  public <X, Y> NodeBuilder<X, Y, S> target(Target<X, Y> target) {
    //noinspection unchecked
    final var casted = (NodeBuilder<X, Y, S>) this;
    casted.target = target;
    return casted;
  }

  /**
   * Sets the suggester.
   *
   * @param suggester the suggester
   */
  public NodeBuilder<T, R, S> suggester(Suggester<T> suggester) {
    this.suggester = suggester;
    return this;
  }

  /**
   * Adds the given child.
   *
   * @param node the node
   * @param <X> the type of the child's input
   * @param <Y> the type of the child's output
   */
  public <X, Y> NodeBuilder<T, R, S> child(Node<X, Y> node) {
    final var identifier = node.identifiers().get(0);
    nodes.put(identifier, node);
    if (node.identifiers().size() > 1) {
      node.identifiers().stream().skip(1).forEach(id -> aliasedNodes.put(id, node));
    }
    return this;
  }

  /**
   * Builds a child and adds it.
   *
   * @param <X> the type of the child's input
   * @param <Y> the type of the child's output
   */
  public <X, Y> NodeBuilder<X, Y, NodeBuilder<T, R, S>> child() {
    return new NodeBuilder<>(() -> this, this::child);
  }

  /** Returns the parent. */
  public S endNode() {
    return end();
  }
}
