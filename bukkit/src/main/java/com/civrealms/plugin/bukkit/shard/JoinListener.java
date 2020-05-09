package com.civrealms.plugin.bukkit.shard;

import com.civrealms.plugin.common.packet.PacketManager;
import com.civrealms.plugin.common.packets.PacketRequestPlayer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class JoinListener implements Listener {
  private final Plugin scheduler;
  private final ShardManager shardManager;
  private final PacketManager packetManager;

  public JoinListener(Plugin scheduler, ShardManager shardManager,
      PacketManager packetManager) {
    this.scheduler = scheduler;
    this.shardManager = shardManager;
    this.packetManager = packetManager;
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void on(PlayerJoinEvent e) {
    Bukkit.getScheduler().runTaskLater(scheduler, () -> {
      // identify if we don't have shards yet
      shardManager.sendIdentify();

      // check for player data
      packetManager.sendProxy(new PacketRequestPlayer(e.getPlayer().getUniqueId()));

      System.out.println("REQUEST >> " + e.getPlayer().getName());
    }, 2);
  }
}
