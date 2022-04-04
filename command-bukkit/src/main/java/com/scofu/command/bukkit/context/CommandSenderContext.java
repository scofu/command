package com.scofu.command.bukkit.context;

import com.scofu.command.bukkit.CommandSenderPermissionHolder;
import com.scofu.command.text.AudienceContext;
import com.scofu.command.text.HelpMessageGenerator;
import com.scofu.command.validation.Permission;
import java.util.Locale;
import org.bukkit.command.CommandSender;

/**
 * A command sender based context.
 */
public class CommandSenderContext extends AudienceContext {

  private final CommandSender commandSender;

  /**
   * Constructs a new command sender based context.
   *
   * @param helpMessageGenerator the help message generator
   * @param commandSender        the command sender
   * @param locale               the locale
   */
  public CommandSenderContext(HelpMessageGenerator helpMessageGenerator,
      CommandSender commandSender, Locale locale) {
    super(commandSender, locale, helpMessageGenerator);
    this.commandSender = commandSender;
    map(Permission.HOLDER_IDENTIFIER).to(new CommandSenderPermissionHolder(commandSender));
  }

  /**
   * Returns the command sender.
   */
  public CommandSender commandSender() {
    return commandSender;
  }
}
