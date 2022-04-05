package com.scofu.command.text;

import com.scofu.command.model.DiscoveryListener;
import com.scofu.command.model.Identified;
import com.scofu.command.model.Node;

/**
 * Attaches descriptions to discovered nodes annotated with {@link Description}.
 */
public class DescriptionDiscoveryListener implements DiscoveryListener {

  @Override
  public <T, R> void onDiscovery(Node<T, R> node, boolean root) {
    node.expand(Identified.METHOD_IDENTIFIER)
        .filter(method -> method.isAnnotationPresent(Description.class))
        .map(method -> method.getAnnotation(Description.class))
        .map(Description::value)
        .ifPresent(description -> node.map(Description.DESCRIPTION_IDENTIFIER).to(description));
  }
}
