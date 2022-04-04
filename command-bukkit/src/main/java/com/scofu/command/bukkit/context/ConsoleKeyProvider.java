package com.scofu.command.bukkit.context;

import java.util.UUID;
import net.kyori.adventure.identity.Identity;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

/**
 * Console key provider.
 */
public class ConsoleKeyProvider implements KeyProvider<ConsoleCommandSender> {

  @Override
  public boolean test(CommandSender commandSender) {
    return commandSender instanceof ConsoleCommandSender;
  }

  @Override
  public UUID provide(ConsoleCommandSender consoleCommandSender) {
    return Identity.nil().uuid();
  }
}
