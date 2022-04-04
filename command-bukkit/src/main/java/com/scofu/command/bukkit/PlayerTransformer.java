package com.scofu.command.bukkit;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.Component.translatable;

import com.google.inject.Inject;
import com.scofu.command.Context;
import com.scofu.command.ParameterArgumentException;
import com.scofu.command.Parameters;
import com.scofu.command.Result;
import com.scofu.command.bukkit.context.CommandSenderContext;
import com.scofu.command.model.Parameter;
import com.scofu.command.target.Arguments;
import com.scofu.command.target.Command;
import com.scofu.command.target.Transformer;
import java.lang.reflect.Type;
import java.util.UUID;
import java.util.stream.Stream;
import org.bukkit.Server;
import org.bukkit.entity.Player;

/**
 * Transforms players.
 */
public class PlayerTransformer implements Transformer<Player> {

  private final Server server;

  @Inject
  PlayerTransformer(Server server) {
    this.server = server;
  }

  @Override
  public boolean test(Type type) {
    return type instanceof Class rawType && Player.class.isAssignableFrom(rawType);
  }

  @Override
  public Result<Player> transform(Command command, Parameter<Player> parameter,
      Parameters parameters, Arguments arguments) {
    if (parameter.isAnnotationPresent(Source.class)) {
      if (command.context() instanceof CommandSenderContext commandSenderContext) {
        if (commandSenderContext.commandSender() instanceof Player player) {
          return Result.value(player);
        }
      }
      return Result.error(
          new ParameterArgumentException(translatable("player.transform.sender_not_player"),
              parameter));
    }

    if (!arguments.hasNext()) {
      return Result.empty();
    }

    return arguments.nextQuotable(parameter)
        .flatMap(string -> parsePlayer(string, command.context(), parameter));
  }

  @SuppressWarnings("unchecked")
  @Override
  public Stream<String> suggest(Command command, Parameter<Player> parameter, Parameters parameters,
      Result<String> argument) {
    if (argument.hasError()) {
      return Stream.of("\"");
    }
    if (command.context() instanceof CommandSenderContext commandSenderContext
        && commandSenderContext.commandSender() instanceof Player sender) {
      return server.getOnlinePlayers().stream().filter(sender::canSee).map(Player::getName);
    }
    return server.getOnlinePlayers().stream().map(Player::getName);
  }


  @Override
  public boolean ignoresSuggestionsForParameter(Parameter<Player> parameter) {
    if (parameter.isAnnotationPresent(Source.class)) {
      return true;
    }
    return Transformer.super.ignoresSuggestionsForParameter(parameter);
  }

  private Result<Player> parsePlayer(String string, Context context, Parameter<Player> parameter) {
    if (string.length() > 16) {
      UUID uuid;
      try {
        uuid = UUID.fromString(string);
      } catch (IllegalArgumentException e) {
        return Result.error(new ParameterArgumentException(
            translatable("player.transform.uuid.invalid", text(string)), parameter));
      }
      final var player = server.getPlayer(uuid);
      return makeSureExistsAndVisible(string, context, parameter, player,
          "player.transform.uuid.unknown");
    }
    final var player = server.getPlayer(string);
    return makeSureExistsAndVisible(string, context, parameter, player,
        "player.transform.name.unknown");
  }

  private Result<Player> makeSureExistsAndVisible(String string, Context context,
      Parameter<Player> parameter, Player player, String translation) {
    if (player == null || (context instanceof CommandSenderContext commandSenderContext
        && commandSenderContext.commandSender() instanceof Player sender
        && !sender.canSee(player))) {
      return Result.error(
          new ParameterArgumentException(translatable(translation, text(string)), parameter));
    }
    return Result.value(player);
  }
}
