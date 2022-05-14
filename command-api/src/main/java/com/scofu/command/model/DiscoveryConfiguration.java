package com.scofu.command.model;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Configuration builder for {@link TransformingNodeDiscoverer}. */
public interface DiscoveryConfiguration {

  /** Creates and returns a new default configuration. */
  static DiscoveryConfiguration newDefaultConfiguration() {
    return builder()
        .withExecutorService(
            Executors.newCachedThreadPool(
                new ThreadFactoryBuilder().setNameFormat("discovered-node-pool-%d").build()))
        .build();
  }

  /** Creates and returns a new builder. */
  static DiscoveryConfigurationBuilder builder() {
    return new DiscoveryConfigurationBuilder();
  }

  /** Returns the executor service. */
  ExecutorService executorService();
}
