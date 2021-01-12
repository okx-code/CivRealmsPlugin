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
/*
  @EventHandler
  public void opr(PlayerRespawnEvent event) {
    if (event.isBedSpawn()) {
      LOG.info("CivBungee Tele-metry: OPR, has bed. " + event.getPlayer().getDisplayName());
      this.cpt.getTagManager().tag(event.getPlayer(), null);
    } else if (!getConfig().getString("thisServer").contentEquals(getConfig().getString("upper")) &&
        !ExilePearlPlugin.getApi().isPlayerExiled(event.getPlayer()) &&
        !getConfig().getString("thisServer").contains("prison")) {
      LOG.info("CivBungee Tele-metry: OPR, no bed, upper is thisServer, not Prison, not exiled. " + event.getPlayer().getDisplayName());
      getLogger().info("death");
      ByteArrayDataOutput out1 = ByteStreams.newDataOutput();
      out1.writeUTF("Forward");
      out1.writeUTF(getConfig().getString("upper"));
      out1.writeUTF("death");
      ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
      DataOutputStream msgout = new DataOutputStream(msgbytes);
      try {
        msgout.writeUTF(event.getPlayer().getDisplayName());
        msgout.writeBoolean(true);
      }
      catch (IOException e) {
        e.printStackTrace();
      }

      out1.writeShort(msgbytes.toByteArray().length);
      out1.write(msgbytes.toByteArray());
      event.getPlayer().sendPluginMessage(this, "BungeeCord", out1.toByteArray());

      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF("Connect");
      out.writeUTF(getConfig().getString("upper"));
      event.getPlayer().sendPluginMessage(this, "BungeeCord", out.toByteArray());
      event.getPlayer().getInventory().clear();
      this.cpt.getTagManager().untag(event.getPlayer().getUniqueId());
      LOG.info("CivBungee Tele-metry: out to String " + out.toString());
    } else {
      this.cpt.getTagManager().tag(event.getPlayer(), null);
      LOG.info("CivBungee Tele-metry: OPR, other/else " + event.getPlayer().getDisplayName());
    }
  }*/

/*
     try {
        String tpplayer = msgin.readUTF();
        boolean isdead = msgin.readBoolean();
        playerDead.put(tpplayer, Boolean.valueOf(isdead));
        }
  @EventHandler
  public void opj(PlayerJoinEvent event) {
    tpPlayerTimeout.put(event.getPlayer().getDisplayName(), Long.valueOf(System.currentTimeMillis()));
    if (playerDead.containsKey(event.getPlayer().getDisplayName())) {
      event.getPlayer().getInventory().clear();

      if (event.getPlayer().getBedSpawnLocation() != null) {
        event.getPlayer().teleport(event.getPlayer().getBedSpawnLocation());
        LOG.info("CivBungee Tele-metry: OPJ, dead, has bed. " + event.getPlayer().getDisplayName());
      } else {
          LOG.info("CivBungee Tele-metry: OPJ, dead, no bed. " + event.getPlayer().getDisplayName());
        World w = event.getPlayer().getLocation().getWorld();
        int xspwn = (int)(Math.random() * getConfig().getInt("spawnWidth") +
          getConfig().getInt("spawnXcenter") - (getConfig().getInt("spawnWidth") / 2));
        int zspwn = (int)(Math.random() * getConfig().getInt("spawnHeight") +
          getConfig().getInt("spawnZcenter") - (getConfig().getInt("spawnHeight") / 2));

        Material mat = w.getBlockAt(xspwn, w.getHighestBlockYAt(xspwn, zspwn) - 1, zspwn).getType();
        while (mat.equals(Material.STATIONARY_WATER) || mat.equals(Material.STATIONARY_LAVA) ||
          mat.equals(Material.LAVA)) {
          xspwn = (int)(Math.random() * getConfig().getInt("spawnWidth") +
            getConfig().getInt("spawnXcenter") - (getConfig().getInt("spawnWidth") / 2));
          zspwn = (int)(Math.random() * getConfig().getInt("spawnHeight") +
            getConfig().getInt("spawnZcenter") - (getConfig().getInt("spawnHeight") / 2));

          mat = w.getBlockAt(xspwn, w.getHighestBlockYAt(xspwn, zspwn) - 1, zspwn).getType();
        }

        getLogger().info(String.valueOf(w.getBlockAt(xspwn, w.getHighestBlockYAt(xspwn, zspwn) - 1, zspwn).getType().name()) +
            " : " + xspwn + ", " + zspwn);
        Location l = event.getPlayer().getLocation();
        l.setX(xspwn);
        l.setY(w.getHighestBlockYAt(xspwn, zspwn));
        l.setZ(zspwn);
        event.getPlayer().teleport(l);
        LOG.info("CivBungee Tele-metry: teleport location: " + l.getX() + " " + l.getY() + " " + l.getZ() + " " + event.getPlayer().getDisplayName());
      }
      playerDead.remove(event.getPlayer().getDisplayName());
    }
    LOG.info("CivBungee Tele-metry: OPJ, not dead. "  + event.getPlayer().getDisplayName());
  }
*/
