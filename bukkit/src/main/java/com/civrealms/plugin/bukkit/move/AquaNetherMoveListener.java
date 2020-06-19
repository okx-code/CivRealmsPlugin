package com.civrealms.plugin.bukkit.move;

import com.civrealms.plugin.bukkit.shard.JoinShardManager;
import com.civrealms.plugin.bukkit.shard.LeaveShardManager;
import com.civrealms.plugin.bukkit.shard.ShardManager;
import com.civrealms.plugin.common.packets.PacketPlayerTransfer.TeleportCause;
import com.civrealms.plugin.common.shard.AquaNether;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class AquaNetherMoveListener implements Listener {

  private final ShardManager shardManager;
  private final LeaveShardManager leaveShard;
  private final JoinShardManager joinShardManager;

  public AquaNetherMoveListener(ShardManager shardManager, LeaveShardManager leaveShard, JoinShardManager joinShardManager) {
    this.shardManager = shardManager;
    this.leaveShard = leaveShard;
    this.joinShardManager = joinShardManager;
  }

  @EventHandler
  public void on(PlayerMoveEvent event) {
    if (!isDifferentY(event.getFrom(), event.getTo())) {
      return;
    }
    if (leaveShard.isLeaving(event.getPlayer())) {
      return;
    }
    if (joinShardManager.isJoining(event.getPlayer().getUniqueId())) {
      return;
    }
    AquaNether nether = shardManager.getAquaNether();
    if (nether == null) {
      return;
    }

    int y = event.getTo().getBlockY();

    int yTeleport = (int) nether.getYTeleport();

    // TODO check if portal exists for anti hacking or something
    if (nether.isTop()) {
      if (y <= yTeleport && nether.getOppositeServer() != null) {
        teleportToAquaNether(event.getPlayer(), nether.getOppositeServer());
      }
    } else {
      if (y >= yTeleport) {
        int x = event.getTo().getBlockX();
        int z = event.getTo().getBlockZ();
        String shard = shardManager.getShard(x, y, z);
        if (shard != null) {
          teleportFromAquaNether(event.getPlayer(), shard);
        }
      }
    }
  }

  private void teleportToAquaNether(Player player, String aquaNetherServer) {
    shardManager.sendPlayer(player, TeleportCause.TO_AN, null, aquaNetherServer, null);
  }

  private void teleportFromAquaNether(Player player, String aquaNetherServer) {
    shardManager.sendPlayer(player, TeleportCause.FROM_AN, null, aquaNetherServer, null);
  }

  private boolean isDifferentY(Location from, Location to) {
    if (from == null || to == null) {
      return false;
    }

    return from.getBlockY() != to.getBlockY();
  }
}
