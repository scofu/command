package com.scofu.command.text;

import com.scofu.command.model.Node;
import net.kyori.adventure.text.Component;

/**
 * Generates usage.
 */
public interface UsageGenerator {

  /**
   * Generates and returns usage.
   *
   * @param node       the node
   * @param path       the path
   * @param parameters the parameters
   * @param <T>        the type of the input
   * @param <R>        the type of the output
   */
  <T, R> Component generate(Node<T, R> node, Component path, Component parameters);

}
