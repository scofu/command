package com.scofu.command;

import com.scofu.command.model.NodeTree;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Global tree of nodes.
 */
public interface Dispatcher extends NodeTree {

  /**
   * Dynamically resolves a node with the given list of objects and dispatches it.
   *
   * <p>The objects are iterated over from start to finish, mapping to identifiers that resolve the
   * node. When a node has been successfully resolved, the remaining objects are reduced to a single
   * argument using the given collector.
   *
   * <p>That argument is the input to the target of the node. See {@link
   * com.scofu.command.target.Target}.
   *
   * @param context   the context
   * @param objects   the objects
   * @param collector the collector
   * @param <T>       the type of the input to the node
   * @param <R>       the type of the output from the node
   */
  <T, U, R> R dispatch(Context context, List<U> objects, Collector<? super U, ?, T> collector);

  /**
   * Splits the given string by spaces and dispatches that.
   *
   * <p>See {@link  Dispatcher#dispatch(Context, List, Collector)}.
   *
   * @param context the context
   * @param string  the string
   * @param <R>     the type of the result
   */
  default <R> R dispatchString(Context context, String string) {
    return dispatch(context, List.of(string.split(" ")), Collectors.toList());
  }

  /**
   * Dynamically resolves a node with the given list of objects and gives suggestions for it.
   *
   * <p>The objects are iterated over from start to finish, mapping to identifiers that resolve the
   * node. When a node has been successfully resolved, the remaining objects are reduced to a single
   * argument using the given collector.
   *
   * <p>That argument is the input to the suggester of the node. See {@link
   * com.scofu.command.target.Suggester}.
   *
   * @param context   the context
   * @param objects   the objects
   * @param collector the collector
   * @param <T>       the type of the input to the node
   * @param <R>       the type of the output from the node
   */
  <T, U, R> Stream<String> suggest(Context context, List<U> objects,
      Collector<? super U, ?, T> collector);

  /**
   * Splits the given string by spaces and suggests based on that.
   *
   * <p>See {@link  Dispatcher#suggest(Context, List, Collector)}.
   *
   * @param context the context
   * @param string  the string
   */
  default Stream<String> suggestString(Context context, String string) {
    if (string.endsWith(" ")) {
      return suggest(context,
          Stream.<Object>concat(Stream.of(string.split(" ")), Stream.of("")).toList(),
          Collectors.toList());
    }
    return suggest(context, List.of(string.split(" ")), Collectors.toList());
  }
}
