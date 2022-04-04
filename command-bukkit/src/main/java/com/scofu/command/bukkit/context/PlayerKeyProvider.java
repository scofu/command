package com.scofu.command.bukkit.context;

import java.util.UUID;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Player key provider.
 */
public class PlayerKeyProvider implements KeyProvider<Player> {

  @Override
  public boolean test(CommandSender commandSender) {
    return commandSender instanceof Player;
  }

  @Override
  public UUID provide(Player player) {
    return player.getUniqueId();
  }
}
