package com.civrealms.plugin.bukkit.respawn;

import com.civrealms.plugin.bukkit.shard.ShardManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerRespawnListener implements Listener {
  private final ShardManager shardManager;

  public PlayerRespawnListener(ShardManager shardManager) {
    this.shardManager = shardManager;
  }

  @EventHandler
  public void on(PlayerRespawnEvent e) {
    if (e.isBedSpawn()) {
      return;
    }

    if (!shardManager.getAquaNether().isTop()) {
      
    }
  }
}
