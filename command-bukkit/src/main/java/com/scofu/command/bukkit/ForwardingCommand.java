package com.scofu.command.bukkit;

import static com.scofu.command.model.Identifier.identifier;

import com.scofu.command.Dispatcher;
import com.scofu.command.model.Expansion;
import com.scofu.command.model.Identifier;
import com.scofu.command.model.Node;
import com.scofu.command.text.AudienceContext;
import com.scofu.command.text.HelpMessageGenerator;
import com.scofu.command.validation.Permission;
import com.scofu.text.ThemeRegistry;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.identity.Identified;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

final class ForwardingCommand extends BukkitCommand {

  private final Dispatcher dispatcher;
  private final HelpMessageGenerator helpMessageGenerator;
  private final ThemeRegistry themeRegistry;

  ForwardingCommand(Node<?, ?> node, Dispatcher dispatcher,
      HelpMessageGenerator helpMessageGenerator, ThemeRegistry themeRegistry) {
    super(node.identifiers().get(0).toPath());
    this.dispatcher = dispatcher;
    this.helpMessageGenerator = helpMessageGenerator;
    this.themeRegistry = themeRegistry;
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
    final var context = new AudienceContext(commandSender, locale, helpMessageGenerator,
        commandSender instanceof Identified identified ? themeRegistry.byIdentified(identified)
            : themeRegistry.byName("Vanilla").orElseThrow());
    context.map(Permission.HOLDER_IDENTIFIER)
        .to(permission -> commandSender.isOp() || commandSender.hasPermission(permission));
    context.map(identifier("source")).to(commandSender);
    dispatcher.dispatchString(context, command);
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
    final var context = new AudienceContext(commandSender, locale, helpMessageGenerator,
        commandSender instanceof Identified identified ? themeRegistry.byIdentified(identified)
            : themeRegistry.byName("Vanilla").orElseThrow());
    context.map(Permission.HOLDER_IDENTIFIER)
        .to(permission -> commandSender.isOp() || commandSender.hasPermission(permission));
    context.map(identifier("source")).to(commandSender);
    return dispatcher.suggestString(context, command).filter(s -> filtered(s, args)).toList();
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
