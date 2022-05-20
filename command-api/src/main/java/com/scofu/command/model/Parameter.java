package com.scofu.command.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.scofu.command.ForwardingAnnotatedElement;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Represents a single parameter in a command.
 *
 * @param <T> the type of the parameter
 */
public record Parameter<T>(String nameOrTranslation, Type type, AnnotatedElement annotatedElement)
    implements ForwardingAnnotatedElement {

  /** Creates and returns a new builder. */
  public static <T> ParameterBuilder<T, Void> parameter() {
    return new ParameterBuilder<>();
  }

  /**
   * Sets the type.
   *
   * @param type the type
   */
  public Parameter<T> withType(Type type) {
    checkNotNull(type, "type");
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
    checkNotNull(annotatedElement, "annotatedElement");
    return new Parameter<>(nameOrTranslation, type, annotatedElement);
  }

  /** Returns a new builder from this. */
  public ParameterBuilder<T, Parameter<T>> toBuilder() {
    final var result = new AtomicReference<Parameter<T>>();
    return new ParameterBuilder<>(this, result::get, result::set);
  }
}
