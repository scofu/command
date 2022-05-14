package com.scofu.command.internal;

import com.scofu.command.model.DiscoveryConfiguration;
import java.util.concurrent.ExecutorService;

/** Real discovery configuration. */
public class RealDiscoveryConfiguration implements DiscoveryConfiguration {

  private final ExecutorService executorService;

  private RealDiscoveryConfiguration(ExecutorService executorService) {
    this.executorService = executorService;
  }

  /**
   * Creates and returns a new real discovery configuration.
   *
   * @param executorService the executor service
   */
  public static RealDiscoveryConfiguration newRealDiscoveryConfiguration(
      ExecutorService executorService) {
    return new RealDiscoveryConfiguration(executorService);
  }

  @Override
  public ExecutorService executorService() {
    return executorService;
  }
}
