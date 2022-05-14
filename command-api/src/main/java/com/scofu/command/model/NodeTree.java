package com.scofu.command.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

import com.scofu.command.Context;
import com.scofu.command.DispatchException;
import com.scofu.command.DispatchHandleUnvalidatedException;
import com.scofu.command.Result;
import com.scofu.command.validation.Validator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a tree of nodes.
 *
 * <p>See {@link Node}.
 */
public interface NodeTree {

  /** Returns a map of nodes. */
  Map<Identifier<?>, Node<?, ?>> nodes();

  /**
   * Returns an entry set of validated nodes.
   *
   * @param context the context
   * @param validators the validators
   */
  default Set<Entry<Identifier<?>, Node<?, ?>>> nodes(Context context, Set<Validator> validators) {
    checkNotNull(context, "context");
    checkNotNull(validators, "validators");
    return nodes().entrySet().stream()
        .filter(
            entry -> {
              for (var validator : validators) {
                if (!validator.validate(context, entry.getValue())) {
                  return false;
                }
              }
              return true;
            })
        .collect(Collectors.toSet());
  }

  /** Returns a map of aliased nodes. */
  Map<Identifier<?>, Node<?, ?>> aliasedNodes();

  /**
   * Returns an entry set of validated aliased nodes.
   *
   * @param context the context
   * @param validators the validators
   */
  default Set<Entry<Identifier<?>, Node<?, ?>>> aliasedNodes(
      Context context, Set<Validator> validators) {
    checkNotNull(context, "context");
    checkNotNull(validators, "validators");
    return aliasedNodes().entrySet().stream()
        .filter(
            entry -> {
              for (var validator : validators) {
                if (!validator.validate(context, entry.getValue())) {
                  return false;
                }
              }
              return true;
            })
        .collect(Collectors.toSet());
  }

  /**
   * Registers the given node in this tree.
   *
   * @param node the node
   * @param <T> the type of the input to the node
   * @param <R> the type of the output from the node
   */
  default <T, R> void register(Node<T, R> node) {
    checkNotNull(node, "node");
    node.identifiers().forEach(identifier -> nodes().put(identifier, node));
  }

  /**
   * Resolves a single validated node by iterating through the given identifiers.
   *
   * <p>If a node has a child, and we still have another identifier, that identifier is compared to
   * the child's, and continued if they match. If they don't, and the node has a target (non-futile)
   * it will be returned, otherwise continued until no more identifier is left.
   *
   * @param context the context
   * @param validators the validators
   * @param identifiers the identifiers
   */
  default Result<Node<?, ?>> resolveNodeByIdentifiers(
      Context context, Set<Validator> validators, NodeIdentifierIterator identifiers) {
    checkNotNull(context, "context");
    checkNotNull(validators, "validators");
    checkNotNull(identifiers, "identifiers");
    Result<Node<?, ?>> node = null;
    var parentNode = this;
    Identifier<?> parentIdentifier = null;
    while (parentNode != null && identifiers.hasNext(parentNode, parentIdentifier)) {
      final var identifier = identifiers.next(parentNode, parentIdentifier);
      final var isRoot = parentIdentifier == null;
      if (node == null) {
        node = validateDirectChildByIdentifier(context, validators, isRoot, identifier);
        parentNode = node.hasError() ? null : node.get();
        parentIdentifier = identifier;
        continue;
      }
      node =
          node.flatMap(
              x -> x.validateDirectChildByIdentifier(context, validators, isRoot, identifier));
      parentNode = node.hasError() ? null : node.get();
      parentIdentifier = identifier;
    }
    return node == null
        ? Result.error(new DispatchException(translatable("node.resolve.no_match")))
        : node;
  }

  /**
   * Validates a direct child by the given identifier.
   *
   * @param context the context
   * @param validators the validators
   * @param isRoot whether this child is a root
   * @param identifier the identifier
   */
  default Result<Node<?, ?>> validateDirectChildByIdentifier(
      Context context, Set<Validator> validators, boolean isRoot, Identifier<?> identifier) {
    checkNotNull(context, "context");
    checkNotNull(validators, "validators");
    checkNotNull(identifier, "identifier");
    final var node = nodes().getOrDefault(identifier, aliasedNodes().get(identifier));
    if (node == null) {
      if (isRoot) {
        return Result.error(
            new DispatchException(
                translatable("node.resolve.validate.unknown_root", text(identifier.toPath()))));
      }
      return Result.error(
          new DispatchException(
              translatable("node.resolve.validate.unknown_child", text(identifier.toPath()))));
    }
    for (var validator : validators) {
      if (!validator.validate(context, node)) {
        return Result.error(
            new DispatchHandleUnvalidatedException(
                translatable("node.resolve.unvalidated"), node.handle(), node));
      }
    }
    return Result.value(node);
  }
}
