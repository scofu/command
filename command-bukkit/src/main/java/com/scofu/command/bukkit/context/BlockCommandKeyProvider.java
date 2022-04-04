package com.scofu.command.bukkit.context;

import java.util.UUID;
import net.kyori.adventure.identity.Identity;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;

/**
 * Block command key provider.
 */
public class BlockCommandKeyProvider implements KeyProvider<BlockCommandSender> {

  @Override
  public boolean test(CommandSender commandSender) {
    return commandSender instanceof BlockCommandSender;
  }

  @Override
  public UUID provide(BlockCommandSender blockCommandSender) {
    return Identity.nil().uuid();
  }
}
