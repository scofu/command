package com.scofu.command.text;

import com.scofu.command.model.Parameter;
import com.scofu.common.inject.Feature;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;

/**
 * Describes parameters.
 *
 * @param <T> the type of the parameter
 */
public interface Describer<T> extends Feature, Predicate<Type> {

  /**
   * Returns an optional component describing the given parameter.
   *
   * @param parameter the parameter
   */
  Optional<Component> describe(Parameter<T> parameter);
}
