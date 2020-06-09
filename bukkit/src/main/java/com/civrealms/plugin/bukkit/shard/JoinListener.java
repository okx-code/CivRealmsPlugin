package com.civrealms.plugin.bukkit.shard;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class JoinListener implements Listener {
  private final Plugin scheduler;
  private final ShardManager shardManager;
  private final JoinShardManager joinShardManager;

  public JoinListener(Plugin scheduler, ShardManager shardManager,
      JoinShardManager joinShardManager) {
    this.scheduler = scheduler;
    this.shardManager = shardManager;
    this.joinShardManager = joinShardManager;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void on(PlayerJoinEvent e) {
    System.out.println("Player joined: " + e.getPlayer().getName());
    joinShardManager.joined(e.getPlayer());
    joinShardManager.checkJoin();

    Bukkit.getScheduler().runTaskLater(scheduler, () -> {
      // identify if we don't have shards yet
      shardManager.sendIdentify(true);
    }, 2);

  }
  @EventHandler(priority = EventPriority.MONITOR)
  public void on(PlayerQuitEvent e) {
    joinShardManager.left(e.getPlayer());
  }
}
