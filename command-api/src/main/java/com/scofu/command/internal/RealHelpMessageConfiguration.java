package com.scofu.command.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import com.scofu.command.text.HelpMessageConfiguration;
import java.util.Optional;

/** Real help message configuration. */
public class RealHelpMessageConfiguration implements HelpMessageConfiguration {

  private final Optional<String> commandPrefix;

  private RealHelpMessageConfiguration(Optional<String> commandPrefix) {
    this.commandPrefix = commandPrefix;
  }

  /**
   * Creates and returns a new real help message configuration.
   *
   * @param commandPrefix the command prefix
   */
  public static RealHelpMessageConfiguration newRealHelpMessageConfiguration(
      Optional<String> commandPrefix) {
    checkNotNull(commandPrefix, "commandPrefix");
    return new RealHelpMessageConfiguration(commandPrefix);
  }

  @Override
  public Optional<String> commandPrefix() {
    return commandPrefix;
  }
}
