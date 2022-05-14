package com.scofu.command.model;

import com.google.inject.Inject;
import com.scofu.common.inject.AbstractFeatureManager;

/** Explores and registers transforming nodes in all features. */
public class DiscoveryManager extends AbstractFeatureManager {

  private final TransformingNodeDiscoverer transformingNodeDiscoverer;

  @Inject
  DiscoveryManager(TransformingNodeDiscoverer transformingNodeDiscoverer) {
    this.transformingNodeDiscoverer = transformingNodeDiscoverer;
  }

  @Override
  protected void enable() {
    forEach(feature -> transformingNodeDiscoverer.exploreAndRegister(feature.getClass(), feature));
    transformingNodeDiscoverer.clearCache();
  }
}
