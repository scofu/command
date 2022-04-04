package com.scofu.command.standard;

import static net.kyori.adventure.text.Component.translatable;

import com.scofu.command.model.Parameter;
import com.scofu.command.text.Describer;
import java.lang.reflect.Type;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

final class StringDescriber implements Describer<String> {

  @Override
  public boolean test(Type type) {
    return type instanceof Class rawType && String.class.isAssignableFrom(rawType);
  }

  @Override
  public Optional<Component> describe(Parameter<String> parameter) {
    return Optional.of(translatable("string.parameter.description",
        translatable(parameter.nameOrTranslation()).color(NamedTextColor.WHITE)).color(
        NamedTextColor.GRAY));
  }
}
