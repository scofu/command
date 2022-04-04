package com.scofu.command.model;

import java.util.List;
import org.jetbrains.annotations.NotNull;

/**
 * Listens for nodes discovered in {@link TransformingNodeDiscoverer}.
 */
public interface DiscoveryListener extends Comparable<DiscoveryListener> {

  /**
   * Returns a list of dependencies which should be called before this.
   */
  default List<Class<? extends DiscoveryListener>> dependencies() {
    return List.of();
  }

  /**
   * Called when the given node is discovered.
   *
   * @param node the node
   * @param root whether it is a root node or not
   * @param <T>  the type of the input
   * @param <R>  the type of the output
   */
  <T, R> void onDiscovery(Node<T, R> node, boolean root);

  @Override
  default int compareTo(@NotNull DiscoveryListener o) {
    if (o.dependencies().contains(getClass())) {
      return 1;
    } else if (dependencies().contains(o.getClass())) {
      return -1;
    }
    return 0;
  }

}
