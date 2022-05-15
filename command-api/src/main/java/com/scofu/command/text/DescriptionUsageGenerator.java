package com.scofu.command.text;

import static com.google.common.base.Preconditions.checkNotNull;
import static net.kyori.adventure.text.Component.text;

import com.scofu.command.model.Node;
import com.scofu.text.Color;
import net.kyori.adventure.text.Component;

/** Appends description to usage. */
public class DescriptionUsageGenerator implements UsageGenerator {

  @Override
  public <T, R> Component generate(Node<T, R> node, Component path, Component parameters) {
    checkNotNull(node, "node");
    checkNotNull(path, "path");
    checkNotNull(parameters, "parameters");
    return node.expand(Description.DESCRIPTION_IDENTIFIER)
        .map(Component::translatable)
        .map(
            description ->
                path.append(parameters)
                    .append(text(" - "))
                    .append(description.color(Color.BRIGHT_YELLOW)))
        .orElseGet(() -> path.append(parameters));
  }
}
