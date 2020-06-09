package com.civrealms.plugin.bukkit.move;

import com.civrealms.plugin.bukkit.shard.JoinShardManager;
import com.civrealms.plugin.bukkit.shard.LeaveShardManager;
import com.civrealms.plugin.bukkit.shard.ShardManager;
import com.civrealms.plugin.common.packets.PacketPlayerInfo.TeleportCause;
import com.civrealms.plugin.common.packets.data.BoatData;
import com.civrealms.plugin.common.shard.AquaNether;
import java.util.List;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

/**
 * Listens for x-z movement across shards
 */
public class ShardMoveListener implements Listener {

  private final Plugin plugin;
  private final ShardManager manager;
  private final LeaveShardManager leaveShard;
  private final JoinShardManager joinShardManager;

  public ShardMoveListener(Plugin plugin, ShardManager manager, LeaveShardManager leaveShard, JoinShardManager joinShardManager) {
    this.plugin = plugin;
    this.manager = manager;
    this.leaveShard = leaveShard;
    this.joinShardManager = joinShardManager;
  }

  @EventHandler
  public void on(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    if (!isCrossChunk(event.getFrom(), event.getTo())
        || manager == null
        || manager.getShards() == null
        || leaveShard.isLeaving(player)
        || joinShardManager.isJoining(player.getUniqueId())) {
      return;
    }
    AquaNether aquaNether = manager.getAquaNether();
    if (aquaNether != null && !aquaNether.isTop()) {
      // don't try and teleport people in the aqua nether
      return;
    }

    Location to = event.getTo();
    int x = to.getBlockX();
    int y = to.getBlockY();
    int z = to.getBlockZ();

    String destination = manager.getShard(x, y, z);

    if (destination == null || manager.getCurrentShard().equalsIgnoreCase(destination)) {
      return;
    }

    if (player.isInsideVehicle() && player.getVehicle() instanceof Boat) {
      Boat vehicle = (Boat) player.getVehicle();

      List<Entity> passengers = vehicle.getPassengers();
      if (passengers.size() > 0) {
        // send all the passengers at once
        for (int i = 0; i < passengers.size(); i++) {
          Entity entity = passengers.get(i);
          if (entity instanceof Player) {
            Player passenger = (Player) entity;

            boolean isPassenger = i != 0;
            BoatData boat = new BoatData(vehicle.getUniqueId(), isPassenger,
                vehicle.getWoodType().getData());
            System.out.println(
                "OUT >> " + passenger.getName() + ">> PASSENGER? " + isPassenger + " >> " + boat);
            manager.sendPlayer(passenger, TeleportCause.TRANSITIVE, boat, destination);
          }
        }
        vehicle.remove();

        return;
      }
    }
    manager.sendPlayer(player, TeleportCause.TRANSITIVE, null, destination);
  }

  private boolean isCrossChunk(Location from, Location to) {
    if (from == null || to == null) {
      return false;
    }
    Chunk fromChunk = from.getChunk();
    Chunk toChunk = to.getChunk();

    return fromChunk.getX() != toChunk.getX() || fromChunk.getZ() != toChunk.getZ();
  }
}
