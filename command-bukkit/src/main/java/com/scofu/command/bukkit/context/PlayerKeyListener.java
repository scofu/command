package com.scofu.command.bukkit.context;


import com.google.inject.Inject;
import com.scofu.common.inject.Feature;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Invalidates players from the key cache when they quit.
 */
public class PlayerKeyListener implements Listener, Feature {

  private final KeyProviderMap keyProviderMap;

  @Inject
  PlayerKeyListener(KeyProviderMap keyProviderMap) {
    this.keyProviderMap = keyProviderMap;
  }

  @EventHandler
  private void onPlayerQuitEvent(PlayerQuitEvent event) {
    keyProviderMap.invalidate(event.getPlayer());
  }
}
