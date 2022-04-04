package com.scofu.command.bukkit;

import com.scofu.command.validation.PermissionHolder;
import org.bukkit.command.CommandSender;

/**
 * Command sender permission holder.
 */
public class CommandSenderPermissionHolder implements PermissionHolder {

  private final CommandSender commandSender;

  /**
   * Constructs a new command sender permission holder.
   *
   * @param commandSender the command sender
   */
  public CommandSenderPermissionHolder(CommandSender commandSender) {
    this.commandSender = commandSender;
  }

  @Override
  public boolean hasPermission(String permission) {
    return commandSender.isOp() || commandSender.hasPermission(permission);
  }
}
