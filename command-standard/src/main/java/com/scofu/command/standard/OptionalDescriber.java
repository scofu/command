package com.scofu.command.standard;

import com.google.inject.Inject;
import com.scofu.command.model.Parameter;
import com.scofu.command.text.Describer;
import com.scofu.command.text.DescriberMap;
import com.scofu.text.Theme;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import net.kyori.adventure.text.Component;

final class OptionalDescriber implements Describer<Optional> {

  private final DescriberMap describerMap;

  @Inject
  OptionalDescriber(DescriberMap describerMap) {
    this.describerMap = describerMap;
  }

  @Override
  public boolean test(Type type) {
    return type instanceof ParameterizedType parameterizedType
        && parameterizedType.getRawType() instanceof Class rawType
        && Optional.class.isAssignableFrom(rawType);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Optional<Component> describe(Parameter<Optional> parameter, Theme theme) {
    final var argumentType = parameter.type() instanceof ParameterizedType parameterizedType
        && parameterizedType.getActualTypeArguments().length > 0
        ? parameterizedType.getActualTypeArguments()[0] : null;
    return Optional.ofNullable(argumentType)
        .flatMap(describerMap::get)
        .flatMap(describer -> describer.describe((Parameter) parameter, theme));
  }
}
