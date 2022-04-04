package com.scofu.command.target;

import com.scofu.command.Parameters;
import com.scofu.command.Result;
import com.scofu.command.model.Parameter;
import com.scofu.common.inject.Feature;
import java.lang.reflect.Type;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Transforms arguments to an object.
 *
 * @param <T> the type of the object
 */
public interface Transformer<T> extends Feature, Predicate<Type> {

  /**
   * Transforms and returns a result of the transformation.
   *
   * @param command    the command
   * @param parameter  the parameter
   * @param parameters the parameters
   * @param arguments  the arguments
   */
  Result<T> transform(Command command, Parameter<T> parameter, Parameters parameters,
      Arguments arguments);

  /**
   * Transforms and returns a stream of suggestions for the transformation.
   *
   * @param command    the command
   * @param parameter  the parameter
   * @param parameters the parameters
   * @param argument   the argument
   */
  default Stream<String> suggest(Command command, Parameter<T> parameter, Parameters parameters,
      Result<String> argument) {
    return Stream.empty();
  }

  /**
   * Returns whether this transformer ignores suggestions for the given parameter.
   *
   * @param parameter the parameter
   */
  default boolean ignoresSuggestionsForParameter(Parameter<T> parameter) {
    return false;
  }

}
