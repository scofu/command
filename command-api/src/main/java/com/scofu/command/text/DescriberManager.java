package com.scofu.command.text;

import com.google.inject.Inject;
import com.scofu.common.inject.AbstractFeatureManager;

/** Manages {@link Describer} features. */
public class DescriberManager extends AbstractFeatureManager {

  private final DescriberMap describerMap;

  @Inject
  DescriberManager(DescriberMap describerMap) {
    this.describerMap = describerMap;
  }

  @Override
  protected void enable() {
    streamWithType(Describer.class).forEach(describerMap::add);
  }
}
