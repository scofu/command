package com.scofu.command.model;

import com.scofu.command.target.Suggester;
import com.scofu.command.target.Target;
import com.scofu.common.Expandable;
import com.scofu.common.ExpansionMap;
import com.scofu.common.Identifier;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;

/**
 * Represents a single command node.
 *
 * <p>A node without a handle, target or suggester is futile, which can be useful for virtual nodes
 * that shouldn't be executable.
 *
 * <p>In this example table of nodes, it can make sense for the root node ('abc') to be futile.
 *
 * <table>
 *   <tr>
 *     <td>'abc' -></td>
 *     <td>'xyz'</td>
 *   </tr>
 *   <tr>
 *     <td>'abc' -></td>
 *     <td>'123'</td>
 *   </tr>
 * </table>
 *
 * @param <T> the type of the input
 * @param <R> the type of the output
 */
public record Node<T, R>(
    List<Identifier<?>> identifiers,
    @Nullable Handle handle,
    @Nullable Target<T, R> target,
    @Nullable Suggester<T> suggester,
    Map<Identifier<?>, Node<?, ?>> nodes,
    Map<Identifier<?>, Node<?, ?>> aliasedNodes,
    ExpansionMap expansions)
    implements NodeTree, Expandable<Node<T, R>> {

  /**
   * Creates and returns a new builder.
   *
   * @param <T> the type of the input
   * @param <R> the type of the output
   */
  public static <T, R> NodeBuilder<T, R, Void> node() {
    return new NodeBuilder<>();
  }

  /** Returns a new builder from this. */
  public NodeBuilder<T, R, Node<T, R>> toBuilder() {
    final var result = new AtomicReference<Node<T, R>>();
    return new NodeBuilder<>(this, result::get, result::set);
  }
}
