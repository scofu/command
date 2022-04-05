package com.scofu.command.model;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Represents a dynamic expansion of an expandable object.
 *
 * @param <T> the type of the value
 */
public interface Expansion<T> {

  /**
   * Returns an empty expansion.
   *
   * @param <T> the type of the value
   */
  @SuppressWarnings("unchecked")
  static <T> Expansion<T> empty() {
    return Empty.EMPTY;
  }

  /**
   * Creates and returns a new expansion with the given value.
   *
   * @param value the value
   * @param <T>   the type of the value
   */
  static <T> Expansion<T> value(T value) {
    return new Value<>(Optional.of(value));
  }

  /**
   * Creates and returns a new optional expansion with the given optonal value.
   *
   * @param value the value
   * @param <T>   the type of the value
   */
  static <T> Expansion<T> optional(Optional<T> value) {
    return new Value<>(value);
  }

  /**
   * Creates and returns a new lazy expansion with the given supplier.
   *
   * @param supplier the vsupplieralue
   * @param <T>      the type of the value
   */
  static <T> Expansion<T> lazy(Supplier<T> supplier) {
    return new Lazy<>(supplier);
  }

  /**
   * Returns the optional value.
   */
  Optional<T> get();

  /**
   * See {@link Optional#orElseThrow()}.
   */
  default T orElseThrow() {
    return get().orElseThrow();
  }

  /**
   * Empty expansion.
   */
  class Empty implements Expansion {

    private static final Empty EMPTY = new Empty();

    @Override
    public Optional get() {
      return Optional.empty();
    }

  }

  /**
   * Value expansion.
   *
   * @param <T> the type of the value
   */
  class Value<T> implements Expansion<T> {

    private final Optional<T> value;

    public Value(Optional<T> value) {
      this.value = value;
    }

    @Override
    public Optional<T> get() {
      return value;
    }
  }

  /**
   * Lazy expansion.
   *
   * @param <T> the type of the value
   */
  class Lazy<T> implements Expansion<T> {

    private final Supplier<T> supplier;

    public Lazy(Supplier<T> supplier) {
      this.supplier = supplier;
    }

    @Override
    public Optional<T> get() {
      return Optional.of(supplier.get());
    }
  }

}
