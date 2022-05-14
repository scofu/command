package com.scofu.command.internal;

import com.scofu.command.model.Expansion;
import com.scofu.command.model.Handle;
import com.scofu.command.model.HandleBuilder;
import com.scofu.command.model.Identifier;
import com.scofu.command.model.Node;
import com.scofu.command.model.NodeBuilder;
import com.scofu.command.target.Suggester;
import com.scofu.command.target.Target;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

/** Real node builder. */
public class RealNodeBuilder<T, R> implements NodeBuilder<T, R> {

  private final Map<Identifier<?>, Node<?, ?>> children;
  private final Map<Identifier<?>, Node<?, ?>> aliasedChildren;
  private final Consumer<Node<?, ?>> consumer;
  private final List<? extends Identifier<?>> identifiers;
  private Map<Identifier<?>, Expansion<?>> expansions;
  private NodeBuilder<?, ?> parent;
  private Handle handle;
  private Target<?, ?> target;
  private Suggester<?> suggester;

  private RealNodeBuilder(
      NodeBuilder<?, ?> parent,
      Consumer<Node<?, ?>> consumer,
      List<? extends Identifier<?>> identifiers) {
    this.parent = parent;
    this.consumer = consumer;
    this.identifiers = identifiers;
    this.children = new ConcurrentHashMap<>();
    this.aliasedChildren = new ConcurrentHashMap<>();
  }

  RealNodeBuilder(
      NodeBuilder<?, ?> parent,
      Map<Identifier<?>, Node<?, ?>> children,
      Map<Identifier<?>, Node<?, ?>> aliasedChildren,
      Consumer<Node<?, ?>> consumer,
      List<? extends Identifier<?>> identifiers,
      Map<Identifier<?>, Expansion<?>> expansions,
      Handle handle,
      Target<?, ?> target,
      Suggester<?> suggester) {
    this.parent = parent;
    this.children = children;
    this.aliasedChildren = aliasedChildren;
    this.consumer = consumer;
    this.identifiers = identifiers;
    this.expansions = expansions;
    this.handle = handle;
    this.target = target;
    this.suggester = suggester;
  }

  /**
   * Creates and returns a new real node builder.
   *
   * @param parent the parent
   * @param consumer the consumer
   * @param identifiers the identifiers
   */
  public static <T, R> RealNodeBuilder<T, R> newRealNodeBuilder(
      NodeBuilder<?, ?> parent,
      Consumer<Node<?, ?>> consumer,
      List<? extends Identifier<?>> identifiers) {
    return new RealNodeBuilder<T, R>(parent, consumer, identifiers);
  }

  @Override
  public NodeBuilder<T, R> withHandle(Handle handle) {
    this.handle = handle;
    return this;
  }

  @Override
  public HandleBuilder<T, R> withHandle() {
    return RealHandleBuilder.newRealHandleBuilder(this, x -> this.handle = x);
  }

  @Override
  public <K, V> NodeBuilder<K, V> withTarget(Target<K, V> target) {
    return new RealNodeBuilder<K, V>(
        parent,
        children,
        aliasedChildren,
        consumer,
        identifiers,
        expansions,
        handle,
        target,
        suggester);
  }

  @Override
  public NodeBuilder<T, R> withSuggester(Suggester<T> suggester) {
    this.suggester = suggester;
    return this;
  }

  @Override
  @SafeVarargs
  public final <K, V, U> NodeBuilder<K, V> withChild(
      Identifier<U> identifier, Identifier<U>... aliases) {
    return newRealNodeBuilder(
        this,
        node -> {
          children.put(identifier, node);
          Stream.of(aliases).forEach(alias -> aliasedChildren.put(alias, node));
        },
        identifiers);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public NodeBuilder<?, ?> endChild() {
    consumer.accept(
        new Node(
            identifiers,
            handle,
            target,
            suggester,
            children,
            aliasedChildren,
            expansions == null ? new ConcurrentHashMap<>() : expansions));
    return parent;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @Override
  public Node<T, R> build() {
    while (parent != null) {
      parent = endChild();
    }
    return new Node(
        identifiers,
        handle,
        target,
        suggester,
        children,
        aliasedChildren,
        expansions == null ? new ConcurrentHashMap<>() : expansions);
  }

  @Override
  public Map<Identifier<?>, Expansion<?>> expansions() {
    if (expansions == null) {
      expansions = new ConcurrentHashMap<>();
    }
    return expansions;
  }
}
