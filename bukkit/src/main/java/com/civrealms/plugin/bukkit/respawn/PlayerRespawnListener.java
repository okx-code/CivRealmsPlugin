package com.civrealms.plugin.bukkit.respawn;

import com.civrealms.plugin.bukkit.inventory.log.InventoryLogger;
import com.civrealms.plugin.bukkit.shard.ShardManager;
import com.civrealms.plugin.common.packets.PacketPlayerTransfer.TeleportCause;
import com.civrealms.plugin.common.shard.AquaNether;
import com.devotedmc.ExilePearl.ExilePearlPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

public class PlayerRespawnListener implements Listener {
  private final Plugin plugin;
  private final InventoryLogger invLogger;
  private final ShardManager shardManager;
  private final BukkitRandomSpawn randomSpawn;

  public PlayerRespawnListener(Plugin plugin, InventoryLogger invLogger, ShardManager shardManager,
      BukkitRandomSpawn randomSpawn) {
    this.plugin = plugin;
    this.invLogger = invLogger;
    this.shardManager = shardManager;
    this.randomSpawn = randomSpawn;
  }

  @EventHandler
  public void on(PlayerRespawnEvent e) {
    Player player = e.getPlayer();
    if (Bukkit.getPluginManager().isPluginEnabled("ExilePearl")
        && ExilePearlPlugin.getApi().isPlayerExiled(player.getUniqueId())) {
      return;
    }

    AquaNether aquaNether = shardManager.getAquaNether();

    Location location = player.getLocation();
    String shard = shardManager.getShard(location.getBlockX(), location.getBlockY(), location.getBlockZ());

    if (aquaNether.isTop()) {
      if (shardManager.getCurrentShard().equals(shardManager.getTransitiveShard())) {
        // if they die in the ocean, spawn them in the main shard for now
        shardManager.sendPlayer(player, TeleportCause.DEATH, null, shardManager.getDeathShard(), null);
        return;
      }

      // They died on the main island world so just spawn them randomly there
      plugin.getLogger().info("Out die");
      if (!e.isBedSpawn()) {
        e.setRespawnLocation(randomSpawn.getLocation(e.getPlayer().getWorld()));
      }
      return;
    }

    /*
    When they die in the Aqua Nether: check if they are within the same radius circle of any proper island’s hitbox above (I am including original continent here as an "island") versus if they are under the open ocean:
      If they are under a proper island above check if they have a bed set in the AN: if that bed is within the same circle as the island boundaries above, spawn them in AN (which will end up being in that bed, but that’s Crimeo’s code taking it from there).
      If they are under a proper island above, and they either have no bed or have a bed set in the AN but that bed is OUTSIDE the circle of the island above, spawn them in the island above, not in the AN.
      If they are under the open ocean, check which islands they have ever visited before, and treat them as if they died under the circle of the island that is closest to their current position among the set of islands that they have visited before (as per two subsections immediately above this).
    */

    // If they die in the AN under the open ocean, treat it like dying in the main shard.
    if (shard.equals(shardManager.getTransitiveShard())) {
      shard = shardManager.getDeathShard();
    }

    if (e.isBedSpawn()) {
      Location bedLocation = e.getRespawnLocation();
      String bedShard = shardManager.getShard(bedLocation.getBlockX(), bedLocation.getBlockY(), bedLocation.getBlockZ());
      if (shard.equals(bedShard)) {
        // if the bed is in the same "top island shard" location, let them spawn there.
        return;
      }
      // otherwise, let them spawn them in the top shard
    }

    plugin.getLogger().info("death sending to " + shard);

    // if you died in the aqua nether
    // spawn in the overworld

    // set their respawn location to where they died while we wait for bungeecord to transfer the player
    e.setRespawnLocation(location);

    shardManager.sendPlayer(player, TeleportCause.DEATH, null, shard, null);
  }

  @EventHandler
  public void on(PlayerDeathEvent e) {
    invLogger.log(e.getEntity(), e.getEntity().getInventory().getContents(), "PLAYER_DEATH");
  }
}
