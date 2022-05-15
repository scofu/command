package com.scofu.command.standard;

import static net.kyori.adventure.text.Component.translatable;

import com.scofu.command.model.Parameter;
import com.scofu.command.text.Describer;
import com.scofu.text.Color;
import java.lang.reflect.Type;
import java.util.Optional;
import net.kyori.adventure.text.Component;

final class EnumDescriber implements Describer<Enum> {

  @Override
  public boolean test(Type type) {
    return type instanceof Class rawType && rawType.isEnum();
  }

  @Override
  public Optional<Component> describe(Parameter<Enum> parameter) {
    return Optional.of(translatable("enum.parameter.description",
        translatable(parameter.nameOrTranslation()).color(Color.BRIGHT_WHITE)));
  }
}
