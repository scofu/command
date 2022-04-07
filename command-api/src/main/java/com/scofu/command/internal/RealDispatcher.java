package com.scofu.command.internal;

import static com.scofu.command.model.Identifier.identifier;
import static net.kyori.adventure.text.Component.translatable;

import com.google.inject.Inject;
import com.scofu.command.Context;
import com.scofu.command.DispatchException;
import com.scofu.command.Dispatcher;
import com.scofu.command.PeekableIterator;
import com.scofu.command.model.Identifier;
import com.scofu.command.model.Node;
import com.scofu.command.model.NodeIdentifierIterator;
import com.scofu.command.model.NodeTree;
import com.scofu.command.target.Command;
import com.scofu.command.validation.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Real dispatcher.
 */
public class RealDispatcher implements Dispatcher {

  private final Map<Identifier<?>, Node<?, ?>> nodes;
  private final Map<Identifier<?>, Node<?, ?>> aliasedNodes;
  private final Set<Validator> validators;

  @Inject
  RealDispatcher(Set<Validator> validators) {
    this.validators = validators;
    this.nodes = new ConcurrentHashMap<>();
    this.aliasedNodes = new ConcurrentHashMap<>();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T, U, R> R dispatch(Context context, List<U> objects,
      Collector<? super U, ?, T> collector) {
    final var identifiersOrArguments = PeekableIterator.wrap(objects.iterator());
    if (!identifiersOrArguments.hasNext()) {
      throw new DispatchException(translatable("dispatch.no_identifiers_or_arguments"));
    }
    final var identifiers = new ArrayList<Identifier<?>>();
    final var result = resolveNodeByIdentifiers(context, validators,
        NodeIdentifierIterator.dynamic(identifiersOrArguments, identifiers::add));
    final var lastTestedIdentifier = identifiersOrArguments.peek().map(Identifier::of).orElse(null);
    final var argument =
        identifiersOrArguments.hasNext() ? Stream.iterate(identifiersOrArguments.next(),
                Objects::nonNull,
                unused -> identifiersOrArguments.hasNext() ? identifiersOrArguments.next() : null)
            .collect(collector) : null;
    if (result.hasError()) {
      return context.onDispatchResolveError(identifiers, argument, result.error());
    }
    final var node = (Node<T, R>) result.get();
    final var target = node.target();
    if (target == null) {
      return context.onDispatchNoTarget(identifiers, node, argument, lastTestedIdentifier);
    }
    if (node.handle() == null) {
      return context.onDispatchNoHandle(identifiers, node, argument);
    }
    try {
      return target.invoke(new Command(context, identifiers, node), argument);
    } catch (Throwable t) {
      System.out.println(argument);
      return context.onDispatchInvokeError(identifiers, node, argument, t);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T, U, R> Stream<String> suggest(Context context, List<U> objects,
      Collector<? super U, ?, T> collector) {
    final var identifiersOrArguments = PeekableIterator.wrap(objects.iterator());
    if (!identifiersOrArguments.hasNext()) {
      throw new DispatchException(translatable("dispatch.no_identifiers_or_arguments"));
    }
    final var lastParent = new AtomicReference<NodeTree>(this);
    final var result = resolveNodeByIdentifiers(context, validators,
        newParentTrackingIterator(identifiersOrArguments, lastParent));
    if (result.hasError()) {
      System.out.println(lastParent.get().nodes(context, validators));
      return Stream.of(lastParent.get().nodes(context, validators).stream(),
              lastParent.get().aliasedNodes(context, validators).stream())
          .flatMap(Function.identity())
          .map(Entry::getKey)
          .map(Identifier::toPath);
    }
    final var argument =
        identifiersOrArguments.hasNext() ? Stream.iterate(identifiersOrArguments.next(),
                Objects::nonNull,
                unused -> identifiersOrArguments.hasNext() ? identifiersOrArguments.next() : null)
            .collect(collector) : null;
    final var node = (Node<T, R>) result.get();
    final var suggester = node.suggester();
    final var children = node.nodes().isEmpty() ? Stream.<String>empty()
        : Stream.concat(node.nodes(context, validators).stream(),
                node.aliasedNodes(context, validators).stream())
            .map(Entry::getKey)
            .map(Identifier::toPath);
    if (suggester == null) {
      return Stream.concat(context.onSuggestNoSuggester(List.of(), node, argument), children);
    }
    if (node.handle() == null) {
      return Stream.concat(context.onSuggestNoHandle(List.of(), node, argument), children);
    }
    final var suggest = suggester.suggest(new Command(context, null, node), argument);
    return Stream.concat(suggest, children);
  }

  @Override
  public Map<Identifier<?>, Node<?, ?>> nodes() {
    return nodes;
  }

  @Override
  public Map<Identifier<?>, Node<?, ?>> aliasedNodes() {
    return aliasedNodes;
  }

  private <T> NodeIdentifierIterator newParentTrackingIterator(
      PeekableIterator<T> identifiersOrArguments, AtomicReference<NodeTree> lastParent) {
    return new NodeIdentifierIterator() {
      @Override
      public boolean hasNext(NodeTree parent, Identifier<?> parentIdentifier) {
        lastParent.set(parent);
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
        return identifier(identifiersOrArguments.next());
      }
    };
  }
}
