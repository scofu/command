package com.scofu.command.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Builds parameters.
 *
 * @param <T> the type of the input to the node
 * @param <R> the type of the output from the node
 */
public interface ParameterBuilder<T, R> {

  ParameterBuilder<T, R> withName(String name);

  ParameterBuilder<T, R> withType(Type type);

  ParameterBuilder<T, R> withAnnotation(Annotation annotation);

  HandleBuilder<T, R> endParameter();
}
