package com.scofu.command.text;

import java.util.Optional;

/**
 * Configuration builder for {@link HelpMessageConfigurationBuilder}.
 */
public interface HelpMessageConfiguration {

  /**
   * Creates and returns a new default configuration.
   */
  static HelpMessageConfiguration newDefaultConfiguration() {
    return builder().build();
  }

  /**
   * Creates and returns a new builder.
   */
  static HelpMessageConfigurationBuilder builder() {
    return new HelpMessageConfigurationBuilder();
  }

  /**
   * Returns the optional command prefix.
   */
  Optional<String> commandPrefix();

}
