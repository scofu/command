package com.scofu.command.text;

import com.scofu.command.internal.RealHelpMessageConfiguration;
import java.util.Optional;

/** Configuration builder for {@link HelpMessageConfiguration}. */
public class HelpMessageConfigurationBuilder {

  private String commandPrefix;

  /**
   * Sets the command prefix.
   *
   * @param commandPrefix the command prefix
   */
  public HelpMessageConfigurationBuilder withCommandPrefix(String commandPrefix) {
    this.commandPrefix = commandPrefix;
    return this;
  }

  /** Builds and returns a new help message configuration. */
  public HelpMessageConfiguration build() {
    return RealHelpMessageConfiguration.newRealHelpMessageConfiguration(
        Optional.ofNullable(commandPrefix));
  }
}
