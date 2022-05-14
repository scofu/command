package com.scofu.command.model;

import static com.scofu.command.model.Identifier.identifier;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.scofu.command.Dispatcher;
import com.scofu.command.target.FutureTarget;
import com.scofu.command.target.MethodTarget;
import com.scofu.command.target.Target;
import com.scofu.command.target.TransformingSuggester;
import com.scofu.command.target.TransformingTarget;
import com.scofu.command.text.Translation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

/** Discovers and registers transforming nodes. */
public class TransformingNodeDiscoverer {

  private final DiscoveryConfiguration discoveryConfiguration;
  private final Dispatcher dispatcher;
  private final TransformingTarget transformingTarget;
  private final TransformingSuggester transformingSuggester;
  private final Map<Identifier<?>, Set<Consumer<Node<?, ?>>>> waitingForParents;
  private final Map<Identifier<?>, Node<?, ?>> cache;
  private final Set<DiscoveryListener> discoveryListeners;

  @Inject
  TransformingNodeDiscoverer(
      DiscoveryConfiguration discoveryConfiguration,
      Dispatcher dispatcher,
      TransformingTarget transformingTarget,
      TransformingSuggester transformingSuggester,
      Set<DiscoveryListener> discoveryListeners) {
    this.discoveryConfiguration = discoveryConfiguration;
    this.dispatcher = dispatcher;
    this.transformingTarget = transformingTarget;
    this.transformingSuggester = transformingSuggester;
    this.discoveryListeners =
        discoveryListeners.stream().sorted().collect(Collectors.toCollection(LinkedHashSet::new));
    this.waitingForParents = new ConcurrentHashMap<>();
    this.cache = Maps.newConcurrentMap();

    System.out.println("discoveryListeners = " + discoveryListeners);
  }

  /**
   * Iterates over all methods declared in the given class and registers nodes from methods
   * annotated with {@link Identified}.
   *
   * @param type the type
   * @param instance the instance
   * @param <T> the type of the instance
   */
  public <T> List<? extends Node<?, ?>> exploreAndRegister(
      Class<? extends T> type, @Nullable T instance) {
    return Stream.of(type.getDeclaredMethods())
        .peek(method -> method.setAccessible(true))
        .filter(
            ((Predicate<Method>) method -> method.isAnnotationPresent(Identified.class))
                .or(method -> method.isAnnotationPresent(MultiIdentified.class)))
        .flatMap(
            method ->
                Stream.of(method.getAnnotationsByType(Identified.class))
                    .map(discoverable -> fromMethod(method, instance, discoverable)))
        .toList();
  }

  /** Clears the cache. */
  public void clearCache() {
    cache.clear();
  }

  /**
   * Creates and returns a transforming node from the given method.
   *
   * @param method the method
   * @param instance the instance
   * @param identified the discoverable
   * @param <T> the type of the instance
   */
  public <T> Node<?, ?> fromMethod(Method method, @Nullable T instance, Identified identified) {
    final var path = identified.value().split(" ");
    final var identifier = identifier(path[path.length - 1]);
    final var fullyQualifiedIdentifier = identifier(String.join(" ", path));
    final var aliases =
        Stream.of(identified.aliases()).map(Identifier::identifier).toArray(Identifier<?>[]::new);
    final var node = convertToNode(method, instance, identified, identifier, aliases);

    cache.put(fullyQualifiedIdentifier, node);
    Optional.ofNullable(waitingForParents.remove(fullyQualifiedIdentifier))
        .ifPresent(consumers -> consumers.forEach(consumer -> consumer.accept(node)));

    final var parent = path.length > 1 ? path[path.length - 2] : null;
    final var root = parent == null;
    discoveryListeners.forEach(discoveryListener -> discoveryListener.onDiscovery(node, root));

    if (root) {
      System.out.println("registering root: " + identifier.toPath());
      dispatcher.register(node);
      return node;
    }

    final var parentIdentifier =
        identifier(Stream.of(path).limit(path.length - 1).collect(Collectors.joining(" ")));
    final var parentNode = cache.get(parentIdentifier);

    if (parentNode != null) {
      parentNode.nodes().put(identifier, node);
      for (var alias : aliases) {
        parentNode.aliasedNodes().put(alias, node);
      }
    } else {
      var consumers = waitingForParents.get(parentIdentifier);
      if (consumers == null) {
        consumers = new CopyOnWriteArraySet<>();
        waitingForParents.put(parentIdentifier, consumers);
      }
      consumers.add(
          realParentNode -> {
            realParentNode.nodes().put(identifier, node);
            for (var alias : aliases) {
              realParentNode.aliasedNodes().put(alias, node);
            }
          });
    }
    return node;
  }

  private <T> Node<?, ?> convertToNode(
      Method method,
      T instance,
      Identified identified,
      Identifier<Object> identifier,
      Identifier<?>[] aliases) {
    final Node<?, ?> node;
    if (identified.futile()) {
      node = Node.builder(identifier, aliases).map(Identified.METHOD_IDENTIFIER).to(method).build();
    } else {
      Target<List<String>, ?> target;
      if (identified.async()) {
        target =
            new FutureTarget<>(
                discoveryConfiguration.executorService(),
                transformingTarget.then(new MethodTarget<>(method, instance)));
      } else {
        target = transformingTarget.then(new MethodTarget<>(method, instance));
      }
      node =
          Node.builder(identifier, aliases)
              .withHandle()
              .withParameters(parseParameters(method))
              .endHandle()
              .map(Identified.METHOD_IDENTIFIER)
              .to(method)
              .withTarget(target)
              .withSuggester(transformingSuggester)
              .build();
    }
    return node;
  }

  private List<Parameter<?>> parseParameters(Method method) {
    return Stream.of(method.getParameters()).<Parameter<?>>map(this::convert).toList();
  }

  private Parameter<Object> convert(java.lang.reflect.Parameter parameter) {
    final var nameOrTranslation =
        Optional.ofNullable(parameter.getAnnotation(Translation.class))
            .map(Translation::value)
            .orElse(parameter.getName());
    return new Parameter<>(nameOrTranslation, parameter.getParameterizedType(), parameter);
  }
}
