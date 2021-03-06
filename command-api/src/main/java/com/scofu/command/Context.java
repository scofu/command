package com.scofu.command;

import static com.scofu.common.ExpansionMap.expansionMap;

import com.scofu.command.model.Node;
import com.scofu.common.Expandable;
import com.scofu.common.ExpansionMap;
import com.scofu.common.Identifier;
import java.util.Locale;
import java.util.stream.Stream;
import javax.annotation.Nullable;

/**
 * Represents the context that is passed along the argument throughout the invocation and or
 * suggestion building process.
 *
 * <p>Can be used to warn and recover from errors, as well as to provide helpful information when
 * something goes wrong.
 */
public interface Context extends Expandable<Context> {

  /** Returns a very simple context that throws and prints errors. */
  static Context simpleContext() {
    return Simple.SIMPLE;
  }

  /** Returns the locale. */
  Locale locale();

  /**
   * Called when an exception occurred whilst resolving a node.
   *
   * @param identifiers the identifers
   * @param argument the argument
   * @param throwable the throwable
   * @param <T> the type of the input
   * @param <R> the type of the output
   */
  <T, R> R onDispatchResolveError(
      Iterable<? extends Identifier<?>> identifiers, T argument, Throwable throwable);

  /**
   * Called when a node was resolved, but it didn't have a target.
   *
   * @param identifiers the identifiers
   * @param node the node
   * @param argument the argument
   * @param lastTestedIdentifier the last tested identifier
   * @param <T> the type of the input
   * @param <R> the type of the output
   */
  <T, R> R onDispatchNoTarget(
      Iterable<? extends Identifier<?>> identifiers,
      Node<T, R> node,
      T argument,
      @Nullable Identifier<?> lastTestedIdentifier);

  /**
   * Called when a node was resolved, but it didn't have a handle.
   *
   * @param identifiers the identifers
   * @param node the node
   * @param argument the argument
   * @param <T> the type of the input
   * @param <R> the type of the output
   */
  <T, R> R onDispatchNoHandle(
      Iterable<? extends Identifier<?>> identifiers, Node<T, R> node, T argument);

  /**
   * Called when an exception occurred whilst invoking a node.
   *
   * @param identifiers the identifiers
   * @param node the node
   * @param argument the argument
   * @param throwable the throwable
   * @param <R> the type of the output
   * @param <T> the type of the input
   */
  <R, T> R onDispatchInvokeError(
      Iterable<? extends Identifier<?>> identifiers,
      Node<T, R> node,
      T argument,
      Throwable throwable);

  /**
   * Called when no suggester was found for a node.
   *
   * @param identifiers the identifiers
   * @param node the node
   * @param argument the argument
   * @param <T> the type of the input
   * @param <R> the type of the output
   */
  default <T, R> Stream<String> onSuggestNoSuggester(
      Iterable<? extends Identifier<?>> identifiers, Node<T, R> node, T argument) {
    return Stream.empty();
  }

  /**
   * Called when a node was resolved, but it didn't have a handle.
   *
   * @param identifiers the identifiers
   * @param node the node
   * @param argument the argument
   * @param <T> the type of the input
   * @param <R> the type of the output
   */
  default <T, R> Stream<String> onSuggestNoHandle(
      Iterable<? extends Identifier<?>> identifiers, Node<T, R> node, T argument) {
    return Stream.empty();
  }

  /**
   * Simple context.
   *
   * @param locale the locale
   * @param expansions the expansions
   */
  record Simple(Locale locale, ExpansionMap expansions) implements Context {

    private static final Simple SIMPLE = new Simple(Locale.US, expansionMap());

    @Override
    public <T, R> R onDispatchResolveError(
        Iterable<? extends Identifier<?>> identifiers, T argument, Throwable throwable) {
      throwable.printStackTrace();
      return null;
    }

    @Override
    public <T, R> R onDispatchNoTarget(
        Iterable<? extends Identifier<?>> identifiers,
        Node<T, R> node,
        T argument,
        @Nullable Identifier<?> lastTestedIdentifier) {
      throw new IllegalStateException("No target for node: " + node);
    }

    @Override
    public <T, R> R onDispatchNoHandle(
        Iterable<? extends Identifier<?>> identifiers, Node<T, R> node, T argument) {
      throw new IllegalStateException("No handle for node: " + node);
    }

    @Override
    public <R, T> R onDispatchInvokeError(
        Iterable<? extends Identifier<?>> identifiers,
        Node<T, R> node,
        T argument,
        Throwable throwable) {
      throwable.printStackTrace();
      return null;
    }
  }
}
