package com.scofu.command.bukkit;

import static net.kyori.adventure.text.Component.translatable;

import com.scofu.command.model.Parameter;
import com.scofu.command.text.Describer;
import java.lang.reflect.Type;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

/**
 * Describes player parameters.
 */
public class PlayerDescriber implements Describer<Player> {

  @Override
  public boolean test(Type type) {
    return type instanceof Class rawType && Player.class.isAssignableFrom(rawType);
  }

  @Override
  public Optional<Component> describe(Parameter<Player> parameter) {
    return Optional.of(
        translatable("player.parameter.description", translatable(parameter.nameOrTranslation())));
  }
}
