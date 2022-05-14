package com.scofu.command.target;

import com.scofu.command.Result;
import com.scofu.command.model.Parameter;
import java.util.stream.Stream;

/** Iterates over arguments. */
public interface Arguments {

  /** Returns whether another argument is present or not. */
  boolean hasNext();

  /** Returns whether another argument is present or if the next argument is present but empty. */
  boolean hasNextOrEmptyNext();

  /** Returns the next argument. */
  String next();

  /**
   * Returns the next quotable argument for the given parameter.
   *
   * @param parameter the parameter
   * @param <T> the type of the parameter
   */
  <T> Result<String> nextQuotable(Parameter<T> parameter);

  /** Returns a stream of the remaining arguments. */
  Stream<String> remaining();
}
