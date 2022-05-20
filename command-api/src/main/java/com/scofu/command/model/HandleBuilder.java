package com.scofu.command.model;

import com.google.common.collect.Lists;
import com.scofu.common.AbstractBuilder;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/**
 * Builds handles.
 *
 * @param <R> the type of the parent
 */
public class HandleBuilder<R> extends AbstractBuilder<Handle, R, HandleBuilder<R>> {

  private List<Parameter<?>> parameters;

  /**
   * Constructs a new handle builder.
   *
   * @param from the from
   * @param parent the parent
   * @param consumer the consumer
   */
  public HandleBuilder(
      @Nullable Handle from, @Nullable Supplier<R> parent, @Nullable Consumer<Handle> consumer) {
    super(from, parent, consumer);
  }

  /**
   * Constructs a new handle builder.
   *
   * @param parent the parent
   * @param consumer the consumer
   */
  public HandleBuilder(@Nullable Supplier<R> parent, @Nullable Consumer<Handle> consumer) {
    super(parent, consumer);
  }

  /**
   * Constructs a new handle builder.
   *
   * @param from the from
   */
  public HandleBuilder(@Nullable Handle from) {
    super(from);
  }

  /** Constructs a new handle builder. */
  public HandleBuilder() {}

  @Override
  protected void initializeFrom(Handle handle) {
    parameters = Lists.newArrayList(handle.parameters());
  }

  @Override
  public Handle build() {
    return new Handle(optional(parameters).orElseGet(List::of));
  }

  /**
   * Adds the given parameter.
   *
   * @param parameter the parameter
   * @param <T> the type of the parameter's type
   */
  public <T> HandleBuilder<R> parameter(Parameter<T> parameter) {
    if (parameters == null) {
      parameters = Lists.newArrayList();
    }
    parameters.add(parameter);
    return this;
  }

  /** Builds a parameter and adds it. */
  public ParameterBuilder<Void, HandleBuilder<R>> parameter() {
    return new ParameterBuilder<>(() -> this, this::parameter);
  }

  /**
   * Sets the parameters.
   *
   * @param parameters the parameters
   */
  public HandleBuilder<R> parameters(List<Parameter<?>> parameters) {
    this.parameters = parameters;
    return this;
  }

  /** Returns the parent. */
  public R endHandle() {
    return end();
  }
}
