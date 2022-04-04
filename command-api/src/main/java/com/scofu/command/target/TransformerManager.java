package com.scofu.command.target;

import com.google.inject.Inject;
import com.scofu.common.inject.AbstractFeatureManager;

/**
 * Manages {@link Transformer} features.
 */
public class TransformerManager extends AbstractFeatureManager {

  private final TransformerMap transformerMap;

  @Inject
  TransformerManager(TransformerMap transformerMap) {
    this.transformerMap = transformerMap;
  }

  @Override
  protected void enable() {
    streamWithType(Transformer.class).forEach(transformerMap::add);
  }
}
