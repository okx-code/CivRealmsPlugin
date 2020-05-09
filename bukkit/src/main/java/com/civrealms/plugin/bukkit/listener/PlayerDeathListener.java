package com.civrealms.plugin.bukkit.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerDeathListener implements Listener {
  @EventHandler
  public void on(PlayerRespawnEvent event) {
    if (event.isBedSpawn()) {
      return;
    }
  }
}
