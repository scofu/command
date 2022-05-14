package com.scofu.command.model;

import com.scofu.command.internal.RealDiscoveryConfiguration;
import java.util.concurrent.ExecutorService;

/** Configuration builder for {@link DiscoveryConfiguration}. */
public class DiscoveryConfigurationBuilder {

  private ExecutorService executorService;

  /**
   * Sets the executor service.
   *
   * @param executorService the executor service
   */
  public DiscoveryConfigurationBuilder withExecutorService(ExecutorService executorService) {
    this.executorService = executorService;
    return this;
  }

  /** Builds and returns a new discovery configuration. */
  public DiscoveryConfiguration build() {
    return RealDiscoveryConfiguration.newRealDiscoveryConfiguration(executorService);
  }
}
