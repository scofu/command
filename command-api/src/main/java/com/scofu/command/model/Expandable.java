package com.scofu.command.model;

import com.scofu.command.internal.RealExpansionBuilder;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a base for things that can be expanded.
 *
 * @param <R> the type of the expandable
 */
public interface Expandable<R extends Expandable<R>> {

  /**
   * Returns the expansions.
   */
  Map<Identifier<?>, Expansion<?>> expansions();

  /**
   * Returns the optional value of an optional expansion with the given identifier.
   *
   * @param identifier the identifier
   * @param <T>        the type of the value
   */
  @SuppressWarnings("unchecked")
  default <T> Optional<T> expand(Identifier<T> identifier) {
    return Optional.ofNullable(expansions().get(identifier))
        .map(o -> (Expansion<T>) o)
        .flatMap(Expansion::get);
  }

  /**
   * Maps the given identifier to an expansion.
   *
   * @param identifier the identifier
   * @param <T>        the type of the identifier
   */
  @SuppressWarnings("unchecked")
  default <T> ExpansionBuilder<T, R> map(Identifier<T> identifier) {
    return RealExpansionBuilder.newRealExpansionBuilder(
        expansion -> expansions().put(identifier, expansion), (R) this);
  }

}
