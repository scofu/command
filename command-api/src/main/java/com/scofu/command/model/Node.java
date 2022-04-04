package com.scofu.command.model;

import com.scofu.command.internal.RealNodeBuilder;
import com.scofu.command.target.Suggester;
import com.scofu.command.target.Target;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.annotation.Nullable;

/**
 * Represents a single command node.
 *
 * <p>A node without a handle, target or suggester is futile, which can be useful for virtual nodes
 * that shouldn't be executable.
 *
 * <p>In this example table of nodes, it can make sense for the root node ('abc') to be futile.
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
public record Node<T, R>(List<Identifier<?>> identifiers,
                         @Nullable Handle handle,
                         @Nullable Target<T, R> target,
                         @Nullable Suggester<T> suggester,
                         Map<Identifier<?>, Node<?, ?>> nodes,
                         Map<Identifier<?>, Node<?, ?>> aliasedNodes,
                         Map<Identifier<?>, Expansion<?>> expansions)
    implements NodeTree, Expandable<Node<T, R>> {

  /**
   * Returns a new builder.
   *
   * @param identifier the identifier
   * @param aliases    the aliases
   * @param <T>        the type of the input to the node
   * @param <R>        the type of the output from the node
   */
  public static <T, R> NodeBuilder<T, R> builder(Identifier<?> identifier,
      Identifier<?>... aliases) {
    return RealNodeBuilder.newRealNodeBuilder(null, null,
        Stream.concat(Stream.of(identifier), Stream.of(aliases)).toList());
  }

}
