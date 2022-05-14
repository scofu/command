package com.scofu.command.model;

import static com.scofu.command.model.Identifier.identifier;

import com.scofu.command.PeekableIterator;
import java.util.function.Consumer;

/** Iterates over node identifiers with information about the parent. */
public interface NodeIdentifierIterator {

  /**
   * Creates and returns a new node identifier iterator.
   *
   * @param identifiers the identifiers
   */
  static NodeIdentifierIterator of(Iterable<? extends Identifier<?>> identifiers) {
    final var iterator = identifiers.iterator();
    return new NodeIdentifierIterator() {
      @Override
      public boolean hasNext(NodeTree parent, Identifier<?> parentIdentifier) {
        return iterator.hasNext();
      }

      @Override
      public Identifier<?> next(NodeTree parent, Identifier<?> parentIdentifier) {
        return iterator.next();
      }
    };
  }

  /**
   * Creates and returns a new dynamic node identifier iterator.
   *
   * @param identifiersOrArguments the identifiers or arguments
   * @param consumer the consumer
   */
  static <T> NodeIdentifierIterator dynamic(
      PeekableIterator<T> identifiersOrArguments, Consumer<? super Identifier<?>> consumer) {
    return new NodeIdentifierIterator() {
      @Override
      public boolean hasNext(NodeTree parent, Identifier<?> parentIdentifier) {
        if (parent.nodes().isEmpty()) {
          return false;
        }
        if (identifiersOrArguments.hasNext()) {
          final var next = identifier(identifiersOrArguments.peek().orElseThrow());
          return parent.nodes().getOrDefault(next, parent.aliasedNodes().get(next)) != null;
        }
        return false;
      }

      @Override
      public Identifier<?> next(NodeTree parent, Identifier<?> parentIdentifier) {
        final var identifier = identifier(identifiersOrArguments.next());
        consumer.accept(identifier);
        return identifier;
      }
    };
  }

  /**
   * Returns whether another node identifier is present or not.
   *
   * @param parent the parent
   * @param parentIdentifier the parent identifier
   */
  boolean hasNext(NodeTree parent, Identifier<?> parentIdentifier);

  /**
   * Returns the next node identifier.
   *
   * @param parent the parent
   * @param parentIdentifier the parent identifier
   */
  Identifier<?> next(NodeTree parent, Identifier<?> parentIdentifier);
}
