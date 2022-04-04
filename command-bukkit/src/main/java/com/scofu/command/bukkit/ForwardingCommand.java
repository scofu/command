package com.scofu.command.bukkit;

import com.scofu.command.Dispatcher;
import com.scofu.command.bukkit.context.KeyProviderMap;
import com.scofu.command.model.Expansion;
import com.scofu.command.model.Identifier;
import com.scofu.command.model.Node;
import com.scofu.command.validation.Permission;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

/**
 * Forwards an internal command to bukkit.
 */
public class ForwardingCommand extends BukkitCommand {

  private final Dispatcher dispatcher;
  private final KeyProviderMap keyProviderMap;

  protected ForwardingCommand(Node<?, ?> node, Dispatcher dispatcher,
      KeyProviderMap keyProviderMap) {
    super(node.identifiers().get(0).toPath());
    this.dispatcher = dispatcher;
    this.keyProviderMap = keyProviderMap;
    setAliases(node.identifiers().stream().skip(1).map(Identifier::toPath).toList());
    System.out.println("node.identifiers() = " + node.identifiers());
    System.out.println("node.expansions() = " + node.expansions()
        .values()
        .stream()
        .map(Expansion::get)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(Object::toString)
        .toList());
    node.expand(Permission.PERMISSION_IDENTIFIER).ifPresent(this::setPermission);
  }

  @Override
  public boolean execute(CommandSender commandSender, String alias, String[] args) {
    final var command = Stream.concat(Stream.of(
            alias.startsWith(ForwardingDiscoveryListener.FALLBACK_PREFIX + ":") ? alias.split(
                ForwardingDiscoveryListener.FALLBACK_PREFIX + ":", 2)[1] : alias), Stream.of(args))
        .collect(Collectors.joining(" "));
    final var locale = getLocale(commandSender);
    keyProviderMap.getOrCreateContext(commandSender, locale)
        .ifPresent(context -> dispatcher.dispatchString(context, command));
    return false;
  }

  @Override
  public List<String> tabComplete(CommandSender commandSender, String alias, String[] args,
      Location location) throws IllegalArgumentException {
    var command = Stream.of(Stream.of(
            alias.startsWith(ForwardingDiscoveryListener.FALLBACK_PREFIX + ":") ? alias.split(
                ForwardingDiscoveryListener.FALLBACK_PREFIX + ":", 2)[1] : alias), Stream.of(args))
        .flatMap(Function.identity())
        .collect(Collectors.joining(" "));
    final var locale = getLocale(commandSender);
    return keyProviderMap.getOrCreateContext(commandSender, locale)
        .map(context -> dispatcher.suggestString(context, command))
        .orElse(Stream.empty())
        .filter(s -> filtered(s, args))
        .toList();
  }

  private boolean filtered(String s, String[] args) {
    if (args.length == 0) {
      return true;
    }
    final var start = args[args.length - 1];
    return s.regionMatches(true, 0, start, 0, start.length());
  }

  private Locale getLocale(CommandSender commandSender) {
    if (commandSender instanceof Player player) {
      return player.locale();
    } else {
      return Locale.US;
    }
  }
}
