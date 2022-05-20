package com.scofu.command.target;

import com.scofu.command.Context;
import com.scofu.command.model.Node;
import com.scofu.common.Identifier;

/**
 * Represents the static state of a command invocation and transformation.
 *
 * @param context the context
 * @param identifiers the identifiers
 * @param node the node
 */
public record Command(
    Context context, Iterable<? extends Identifier<?>> identifiers, Node<?, ?> node) {}
