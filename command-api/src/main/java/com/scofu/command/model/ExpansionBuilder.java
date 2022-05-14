package com.scofu.command.model;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Binds expansions.
 *
 * @param <T> the type of the value
 * @param <R> the type of the parent
 */
public interface ExpansionBuilder<T, R> {

  /**
   * Binds the expansion to the given value.
   *
   * @param value the value
   */
  R to(T value);

  /**
   * Binds the expansion to the given optional value.
   *
   * @param value the value
   */
  R toOptional(Optional<T> value);

  /** Binds the expansion to nothing. */
  R toNothing();

  /**
   * Binds the expansion to the given supplier.
   *
   * @param supplier the supplier
   */
  R toSupplier(Supplier<T> supplier);
}
