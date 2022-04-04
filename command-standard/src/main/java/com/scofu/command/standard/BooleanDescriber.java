package com.scofu.command.standard;

import static net.kyori.adventure.text.Component.translatable;

import com.scofu.command.model.Parameter;
import com.scofu.command.text.Describer;
import java.lang.reflect.Type;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

final class BooleanDescriber implements Describer<Boolean> {

  @Override
  public boolean test(Type type) {
    return type instanceof Class rawType
        && (Boolean.class.isAssignableFrom(rawType) || boolean.class.isAssignableFrom(rawType));
  }

  @Override
  public Optional<Component> describe(Parameter<Boolean> parameter) {
    return Optional.of(translatable("boolean.parameter.description",
        translatable(parameter.nameOrTranslation()).color(NamedTextColor.WHITE)).color(
        NamedTextColor.GRAY));
  }
}
