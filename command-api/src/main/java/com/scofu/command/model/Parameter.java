package com.scofu.command.model;

import com.scofu.command.ForwardingAnnotatedElement;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * Represents a single parameter in a command.
 *
 * @param <T> the type of the parameter
 */
public record Parameter<T>(String nameOrTranslation, Type type,
                           AnnotatedElement annotatedElement) implements
    ForwardingAnnotatedElement {

  /**
   * Sets the type.
   *
   * @param type the type
   */
  public Parameter<T> withType(Type type) {
    return new Parameter<>(nameOrTranslation, type, annotatedElement);
  }

  /**
   * Forwards the type.
   *
   * @param <V> the forwarded type.
   */
  @SuppressWarnings("unchecked")
  public <V> Parameter<V> forwardType() {
    return (Parameter<V>) this;
  }

  /**
   * Sets the annotated element.
   *
   * @param annotatedElement the annotated element
   */
  public Parameter<T> withAnnotatedElement(AnnotatedElement annotatedElement) {
    return new Parameter<>(nameOrTranslation, type, annotatedElement);
  }

}
