package com.scofu.command.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import com.scofu.command.model.HandleBuilder;
import com.scofu.command.model.Parameter;
import com.scofu.command.model.ParameterBuilder;
import com.scofu.common.reflect.ListBasedAnnotatedElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Real parameter builder. */
public class RealParameterBuilder<T, R> implements ParameterBuilder<T, R> {

  private final HandleBuilder<T, R> handleBuilder;
  private final Consumer<Parameter<?>> consumer;
  private String name;
  private Type type;
  private List<Annotation> annotations;

  private RealParameterBuilder(HandleBuilder<T, R> handleBuilder, Consumer<Parameter<?>> consumer) {
    this.handleBuilder = handleBuilder;
    this.consumer = consumer;
  }

  /**
   * Creates and returns a new real parameter builder.
   *
   * @param handleBuilder the handle builder
   * @param consumer the consumer
   */
  public static <T, R> RealParameterBuilder<T, R> newRealParameterBuilder(
      HandleBuilder<T, R> handleBuilder, Consumer<Parameter<?>> consumer) {
    checkNotNull(handleBuilder, "handleBuilder");
    checkNotNull(consumer, "consumer");
    return new RealParameterBuilder<T, R>(handleBuilder, consumer);
  }

  @Override
  public ParameterBuilder<T, R> withName(String name) {
    this.name = name;
    return this;
  }

  @Override
  public ParameterBuilder<T, R> withType(Type type) {
    this.type = type;
    return this;
  }

  @Override
  public ParameterBuilder<T, R> withAnnotation(Annotation annotation) {
    if (annotations == null) {
      annotations = List.of(annotation);
    } else {
      annotations =
          Stream.of(annotations, List.of(annotation))
              .flatMap(Collection::stream)
              .collect(Collectors.toList());
    }
    return this;
  }

  @Override
  public HandleBuilder<T, R> endParameter() {
    consumer.accept(
        new Parameter<>(
            name,
            type,
            new ListBasedAnnotatedElement(annotations == null ? List.of() : annotations)));
    return handleBuilder;
  }
}
