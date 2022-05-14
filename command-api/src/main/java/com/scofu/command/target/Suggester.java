package com.scofu.command.target;

import java.util.stream.Stream;

/**
 * Gives a list of suggestions.
 *
 * @param <T> the type of the input
 */
public interface Suggester<T> {

  /**
   * Returns a list of suggestions.
   *
   * @param command the command
   * @param argument the argument
   */
  Stream<String> suggest(Command command, T argument);
}
