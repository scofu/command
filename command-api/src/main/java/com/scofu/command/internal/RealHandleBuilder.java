package com.scofu.command.internal;

import com.scofu.command.model.Handle;
import com.scofu.command.model.HandleBuilder;
import com.scofu.command.model.NodeBuilder;
import com.scofu.command.model.Parameter;
import com.scofu.command.model.ParameterBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Real handle builder. */
public class RealHandleBuilder<T, R> implements HandleBuilder<T, R> {

  private final NodeBuilder<T, R> nodeBuilder;
  private final Consumer<Handle> consumer;
  private List<Parameter<?>> parameters;

  private RealHandleBuilder(NodeBuilder<T, R> nodeBuilder, Consumer<Handle> consumer) {
    this.nodeBuilder = nodeBuilder;
    this.consumer = consumer;
  }

  /**
   * Creates and returns a new real handle builder.
   *
   * @param nodeBuilder the node builder
   * @param consumer the consumer
   */
  public static <T, R> RealHandleBuilder<T, R> newRealHandleBuilder(
      NodeBuilder<T, R> nodeBuilder, Consumer<Handle> consumer) {
    return new RealHandleBuilder<T, R>(nodeBuilder, consumer);
  }

  @Override
  public ParameterBuilder<T, R> withParameter() {
    return RealParameterBuilder.newRealParameterBuilder(
        this,
        parameter -> {
          if (parameters == null) {
            parameters = List.of(parameter);
          } else {
            parameters =
                Stream.of(parameters, List.of(parameter))
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
          }
        });
  }

  @Override
  public HandleBuilder<T, R> withParameter(String name, Type type, Annotation... annotations) {
    final var parameter =
        new Parameter<>(name, type, new ListBasedAnnotatedElement(List.of(annotations)));
    if (parameters == null) {
      parameters = List.of(parameter);
    } else {
      parameters =
          Stream.of(parameters, List.of(parameter))
              .flatMap(Collection::stream)
              .collect(Collectors.toList());
    }
    return this;
  }

  @Override
  public HandleBuilder<T, R> withParameters(List<Parameter<?>> parameters) {
    if (this.parameters == null) {
      this.parameters = parameters;
    } else {
      this.parameters =
          Stream.of(parameters, parameters)
              .flatMap(Collection::stream)
              .collect(Collectors.toList());
    }
    return this;
  }

  @Override
  public NodeBuilder<T, R> endHandle() {
    consumer.accept(new Handle(parameters == null ? List.of() : parameters));
    return nodeBuilder;
  }
}
