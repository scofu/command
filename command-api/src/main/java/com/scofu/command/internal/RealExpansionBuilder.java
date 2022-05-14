package com.scofu.command.internal;

import com.scofu.command.model.Expansion;
import com.scofu.command.model.ExpansionBuilder;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Real expansion builder. */
public class RealExpansionBuilder<T, R> implements ExpansionBuilder<T, R> {

  private final Consumer<Expansion<T>> consumer;
  private final R returnValue;

  private RealExpansionBuilder(Consumer<Expansion<T>> consumer, R returnValue) {
    this.consumer = consumer;
    this.returnValue = returnValue;
  }

  /**
   * Creates and returns a new real expansion builder.
   *
   * @param consumer the consumer
   * @param returnValue the return value
   */
  public static <T, R> RealExpansionBuilder<T, R> newRealExpansionBuilder(
      Consumer<Expansion<T>> consumer, R returnValue) {
    return new RealExpansionBuilder<T, R>(consumer, returnValue);
  }

  @Override
  public R to(T value) {
    consumer.accept(Expansion.value(value));
    return returnValue;
  }

  @Override
  public R toOptional(Optional<T> value) {
    consumer.accept(Expansion.optional(value));
    return returnValue;
  }

  @Override
  public R toNothing() {
    consumer.accept(Expansion.empty());
    return returnValue;
  }

  @Override
  public R toSupplier(Supplier<T> supplier) {
    consumer.accept(Expansion.lazy(supplier));
    return returnValue;
  }
}
