package com.scofu.command.text;

import static net.kyori.adventure.text.Component.text;

import com.scofu.command.model.Node;
import com.scofu.text.Theme;
import net.kyori.adventure.text.Component;

/**
 * Appends description to usage.
 */
public class DescriptionUsageGenerator implements UsageGenerator {

  @Override
  public <T, R> Component generate(Theme theme, Node<T, R> node, Component path,
      Component parameters) {
    return node.expand(Description.DESCRIPTION_IDENTIFIER)
        .map(Component::translatable)
        .map(description -> path.append(parameters)
            .append(text(" - "))
            .append(description.color(theme.brightYellow())))
        .orElseGet(() -> path.append(parameters));
  }
}
