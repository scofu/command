package com.scofu.command.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Builds handles.
 *
 * @param <T> the type of the input to the node
 * @param <R> the type of the parent
 */
public interface HandleBuilder<T, R> {

  /**
   * Adds a parameter.
   */
  ParameterBuilder<T, R> withParameter();

  /**
   * Adds a parameter.
   *
   * @param name        the name
   * @param type        the type
   * @param annotations the annotations
   */
  HandleBuilder<T, R> withParameter(String name, Type type, Annotation... annotations);

  /**
   * Sets the parameters.
   *
   * @param parameters the parameters.
   */
  HandleBuilder<T, R> withParameters(List<Parameter<?>> parameters);

  /**
   * Sets the handle.
   */
  NodeBuilder<T, R> endHandle();
}
