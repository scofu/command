package com.scofu.command;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents the result of an action. Can be an error or a value, or both.
 *
 * @param <T> the type of the value
 */
public interface Result<T> {

  /**
   * Creates and returns a new error result.
   *
   * @param throwable the throwable
   * @param <T>       the type of the value
   */
  static <T> Result<T> error(Throwable throwable) {
    return new Error<>(throwable);
  }

  /**
   * Returns the error.
   */
  Throwable error();

  /**
   * Creates and returns a new error result with a value.
   *
   * @param throwable the throwable
   * @param value     the value
   * @param <T>       the type of the value
   */
  static <T> Result<T> errorValue(Throwable throwable, T value) {
    return new ErrorValue<>(throwable, value);
  }

  /**
   * Creates and returns a new value result.
   *
   * @param value the value
   * @param <T>   the type of the value
   */
  static <T> Result<T> value(T value) {
    return new Value<>(value);
  }

  /**
   * Creates and returns a new lazy result.
   *
   * @param supplier the supplier
   * @param <T>      the type of the value
   */
  static <T> Result<T> lazy(Supplier<T> supplier) {
    return new Lazy<>(supplier);
  }

  /**
   * Creates and returns a new empty result.
   *
   * @param <T> the type of the value
   */
  static <T> Result<T> empty() {
    return new Value<>(null);
  }

  /**
   * Returns whether this result has a value or not.
   */
  boolean hasValue();

  /**
   * Returns the value.
   */
  T get();

  /**
   * Returns the value if this result is not an error. Otherwise, that error is thrown.
   *
   * @throws Throwable the error
   */
  Optional<T> getOrThrow() throws Throwable;

  /**
   * Returns whether this result has an error or not.
   */
  boolean hasError();

  /**
   * Invokes the given consumer if this result has an error.
   *
   * @param consumer the consumer
   */
  default Result<T> onError(Consumer<Throwable> consumer) {
    if (hasError()) {
      consumer.accept(error());
    }
    return this;
  }

  /**
   * Returns a new result by applying the given function to this result's value.
   *
   * @param function the function
   * @param <R>      the type of the new value
   */
  <R> Result<R> map(Function<T, R> function);

  /**
   * Returns a new result by applying the given function to this result's value.
   *
   * @param function the function
   * @param <R>      the type of the new value
   */
  <R> Result<R> flatMap(Function<T, Result<R>> function);

  /**
   * Error result.
   *
   * @param <T> the type of the value
   */
  class Error<T> implements Result<T> {

    private final Throwable throwable;

    public Error(Throwable throwable) {
      this.throwable = throwable;
    }

    @Override
    public boolean hasValue() {
      return false;
    }

    @Override
    public T get() {
      return null;
    }

    @Override
    public Optional<T> getOrThrow() throws Throwable {
      throw throwable;
    }

    @Override
    public boolean hasError() {
      return true;
    }

    @Override
    public Throwable error() {
      return throwable;
    }

    @Override
    public <R> Result<R> map(Function<T, R> function) {
      return new Error<>(throwable);
    }

    @Override
    public <R> Result<R> flatMap(Function<T, Result<R>> function) {
      return new Error<>(throwable);
    }

    @Override
    public Result<T> onError(Consumer<Throwable> consumer) {
      consumer.accept(throwable);
      return this;
    }
  }

  /**
   * Error value result.
   *
   * @param <T> the type of the value
   */
  class ErrorValue<T> extends Error<T> implements Result<T> {

    private final T value;

    public ErrorValue(Throwable throwable, T value) {
      super(throwable);
      this.value = value;
    }

    @Override
    public boolean hasValue() {
      return true;
    }

    @Override
    public T get() {
      return value;
    }

    @Override
    public <R> Result<R> map(Function<T, R> function) {
      return new ErrorValue<>(error(), function.apply(value));
    }

    @Override
    public <R> Result<R> flatMap(Function<T, Result<R>> function) {
      final var result = map(function).get();
      if (result instanceof Error<R> error) {
        return error;
      }
      return new ErrorValue<>(error(), result.get());
    }
  }

  /**
   * Value result.
   *
   * @param <T> the type of the value
   */
  class Value<T> implements Result<T> {

    private final T value;

    public Value(T value) {
      this.value = value;
    }

    @Override
    public boolean hasValue() {
      return true;
    }

    @Override
    public T get() {
      return value;
    }

    @Override
    public Optional<T> getOrThrow() throws Throwable {
      return Optional.of(value);
    }

    @Override
    public boolean hasError() {
      return false;
    }

    @Override
    public Throwable error() {
      return null;
    }

    @Override
    public <R> Result<R> map(Function<T, R> function) {
      return new Value<>(function.apply(value));
    }

    @Override
    public <R> Result<R> flatMap(Function<T, Result<R>> function) {
      final var result = map(function).get();
      if (result instanceof Error<R> error) {
        return error;
      }
      return result;
    }
  }

  /**
   * Lazy result.
   *
   * @param <T> the type of the value
   */
  class Lazy<T> implements Result<T> {

    private final Supplier<T> supplier;

    public Lazy(Supplier<T> supplier) {
      this.supplier = supplier;
    }

    @Override
    public boolean hasValue() {
      return true;
    }

    @Override
    public T get() {
      return supplier.get();
    }

    @Override
    public Optional<T> getOrThrow() throws Throwable {
      return Optional.of(supplier.get());
    }

    @Override
    public boolean hasError() {
      return false;
    }

    @Override
    public Throwable error() {
      return null;
    }

    @Override
    public <R> Result<R> map(Function<T, R> function) {
      return new Lazy<>(() -> function.apply(supplier.get()));
    }

    @Override
    public <R> Result<R> flatMap(Function<T, Result<R>> function) {
      return map(function.andThen(Result::get));
    }
  }

}
