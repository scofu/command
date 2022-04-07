package com.scofu.command.model;

import java.util.stream.Stream;

/**
 * Identifies nodes and expansions.
 *
 * <p>See {@link NodeTree} and {@link Expandable}.
 *
 * @param <T> the type of the identifier
 */
public interface Identifier<T> {

  /**
   * Creates and returns a new basic identifier.
   *
   * @param value the value
   * @param <T>   the type of the identifier
   * @param <R>   the type of the value
   */
  static <T, R> Identifier<T> of(R value) {
    return new Basic<>(value);
  }

  /**
   * Creates and returns a new basic identifier.
   *
   * @param value the value
   * @param <T>   the type of the identifier
   * @param <R>   the type of the value
   */
  static <T, R> Identifier<T> identifier(R value) {
    return new Basic<>(value);
  }

  /**
   * Creates and returns a chain of basic identifiers.
   *
   * @param first the first identifier
   * @param extra the extra idenfitiers
   */
  static Iterable<? extends Identifier<?>> chain(Object first, Object... extra) {
    return Stream.concat(Stream.of(first), Stream.of(extra)).map(Identifier::of).toList();
  }

  /**
   * Returns the path.
   */
  String toPath();

  /**
   * Basic identifier.
   *
   * @param value the value
   * @param <T>   the type of the value
   * @param <R>   the type of the identifier
   */
  record Basic<T, R>(T value) implements Identifier<R> {

    @Override
    public String toString() {
      return value.toString();
    }

    @Override
    public String toPath() {
      return toString();
    }
  }

}
