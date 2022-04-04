package com.scofu.command.bukkit.context;

import com.google.inject.Inject;
import com.scofu.common.inject.AbstractFeatureManager;

/**
 * Manages {@link KeyProvider} features.
 */
public class KeyProviderManager extends AbstractFeatureManager {

  private final KeyProviderMap keyProviderMap;

  @Inject
  KeyProviderManager(KeyProviderMap keyProviderMap) {
    this.keyProviderMap = keyProviderMap;
  }

  @Override
  protected void enable() {
    streamWithType(KeyProvider.class).forEach(keyProviderMap::add);
  }
}
