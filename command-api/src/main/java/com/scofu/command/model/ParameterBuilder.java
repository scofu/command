package com.scofu.command.model;

import com.google.common.collect.Lists;
import com.scofu.common.AbstractBuilder;
import com.scofu.common.reflect.ListBasedAnnotatedElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/**
 * Builds parameters.
 *
 * @param <T> the type of the parameter's type
 * @param <R> the type of the parent
 */
public class ParameterBuilder<T, R>
    extends AbstractBuilder<Parameter<T>, R, ParameterBuilder<T, R>> {

  private String name;
  private Type type;
  private List<Annotation> annotations;

  /**
   * Constructs a new parameter builder.
   *
   * @param from the from
   * @param parent the parent
   * @param consumer the consumer
   */
  public ParameterBuilder(
      @Nullable Parameter<T> from,
      @Nullable Supplier<R> parent,
      @Nullable Consumer<Parameter<T>> consumer) {
    super(from, parent, consumer);
  }

  /**
   * Constructs a new parameter builder.
   *
   * @param parent the parent
   * @param consumer the consumer
   */
  public ParameterBuilder(@Nullable Supplier<R> parent, @Nullable Consumer<Parameter<T>> consumer) {
    super(parent, consumer);
  }

  /**
   * Constructs a new parameter builder.
   *
   * @param from the from
   */
  public ParameterBuilder(@Nullable Parameter<T> from) {
    super(from);
  }

  /** Constructs a new parameter builder. */
  public ParameterBuilder() {}

  @Override
  protected void initializeFrom(Parameter<T> parameter) {
    name = parameter.nameOrTranslation();
    type = parameter.type();
    annotations = Lists.newArrayList(parameter.getAnnotations());
  }

  /**
   * Sets the name.
   *
   * @param name the name
   */
  public ParameterBuilder<T, R> name(String name) {
    this.name = name;
    return this;
  }

  /**
   * Sets the type.
   *
   * @param type the type
   */
  public ParameterBuilder<T, R> type(Type type) {
    this.type = type;
    return this;
  }

  /**
   * Sets the type.
   *
   * @param type the type
   * @param <V> the type of the new type
   */
  public <V> ParameterBuilder<V, R> type(Class<V> type) {
    final var casted = (ParameterBuilder<V, R>) this;
    casted.type = type;
    return casted;
  }

  /**
   * Adds the given annotation.
   *
   * @param annotation the annotation
   * @param <A> the type of the annotation
   */
  public <A extends Annotation> ParameterBuilder<T, R> annotation(A annotation) {
    if (annotations == null) {
      annotations = Lists.newArrayList();
    }
    annotations.add(annotation);
    return this;
  }

  /** Returns the parent. */
  public R endParameter() {
    return end();
  }

  @Override
  public Parameter<T> build() {
    return new Parameter<>(
        require(name, "name"),
        require(type, "type"),
        optional(annotations)
            .map(ListBasedAnnotatedElement::new)
            .orElseGet(() -> new ListBasedAnnotatedElement(List.of())));
  }
}
