package com.scofu.command.model;

import java.util.List;

/**
 * The parameterized part of a node.
 *
 * <p>Can be shared by multiple nodes.
 */
public record Handle(List<Parameter<?>> parameters) {}
