package com.scofu.command.standard;

import static net.kyori.adventure.text.Component.translatable;

import com.scofu.command.model.Parameter;
import com.scofu.command.text.Describer;
import com.scofu.text.Theme;
import java.lang.reflect.Type;
import java.time.Duration;
import java.util.Optional;
import net.kyori.adventure.text.Component;

final class DurationDescriber implements Describer<Duration> {

  @Override
  public boolean test(Type type) {
    return type instanceof Class rawType && Duration.class.isAssignableFrom(rawType);
  }

  @Override
  public Optional<Component> describe(Parameter<Duration> parameter, Theme theme) {
    return Optional.of(translatable("duration.parameter.description",
        translatable(parameter.nameOrTranslation()).color(theme.brightWhite())).color(
        theme.white()));
  }
}
