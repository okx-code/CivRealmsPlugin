package com.civrealms.plugin.bukkit.shard;

import com.civrealms.plugin.bukkit.inventory.log.InventoryLogger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {
  private final InventoryLogger logger;
  private final JoinShardManager joinShardManager;

  public JoinListener(InventoryLogger logger,
      JoinShardManager joinShardManager) {
    this.logger = logger;
    this.joinShardManager = joinShardManager;
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void on(PlayerJoinEvent e) {
    joinShardManager.joined(e.getPlayer());
    joinShardManager.checkJoin();
    logger.log(e.getPlayer(), e.getPlayer().getInventory().getContents(), "PLAYER_JOIN");

    // not necessary
    /*Bukkit.getScheduler().runTaskLater(scheduler, () -> {
      // identify if we don't have shards yet
      shardManager.sendIdentify(true);
    }, 2);*/

  }
  @EventHandler(priority = EventPriority.MONITOR)
  public void on(PlayerQuitEvent e) {
    joinShardManager.left(e.getPlayer());
    logger.log(e.getPlayer(), e.getPlayer().getInventory().getContents(), "PLAYER_QUIT");
  }
}
