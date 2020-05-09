package com.civrealms.plugin.bukkit.move;

import com.civrealms.plugin.bukkit.boat.BoatInventoryDao;
import com.civrealms.plugin.bukkit.inventory.GZIPInventorySerializer;
import com.civrealms.plugin.bukkit.shard.ShardManager;
import com.civrealms.plugin.common.packet.PacketReceiveEvent;
import com.civrealms.plugin.common.packets.PacketPlayerInfo;
import com.civrealms.plugin.common.packets.PacketPlayerInfo.TeleportCause;
import com.civrealms.plugin.common.packets.data.BoatData;
import com.civrealms.plugin.common.shard.AquaNether;
import com.google.common.eventbus.Subscribe;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class PacketPlayerInfoListener {

  private final PassengerBoatManager passengerBoatManager = new PassengerBoatManager();
  private final Plugin plugin;
  private final ShardManager shardManager;
  private final BoatInventoryDao dao;

  public PacketPlayerInfoListener(Plugin plugin, ShardManager shardManager, BoatInventoryDao dao) {
    this.plugin = plugin;
    this.shardManager = shardManager;
    this.dao = dao;
  }

  @Subscribe
  public void on(PacketReceiveEvent event) {
    if (!(event.getPacket() instanceof PacketPlayerInfo)) {
      return;
    }

    PacketPlayerInfo packet = (PacketPlayerInfo) event.getPacket();

    Player player = Bukkit.getPlayer(packet.getUuid());
    if (player == null) {
      throw new IllegalArgumentException("Could not find player for UUID " + packet.getUuid());
    }

    TeleportCause cause = packet.getCause();
    System.out.println("GOT DATA >> " + player.getName() + " >> " + cause);
    Location destination = new Location(player.getWorld(), packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
    if (cause == TeleportCause.TRANSITIVE) {
      BoatData boatData = packet.getBoat();
      System.out.println("BOAT DATA IS PASSENGER >> " + (boatData == null ? "null" : boatData.isPassenger()));
      if (boatData == null) {
        player.teleport(destination);
      } else if (boatData.isPassenger()) {
        if (passengerBoatManager.addPassenger(boatData.getBoat(), player)) {
          System.out.println(">> PASSENGER SECONDARY");
        } else {
          // teleport for now, they'll get put into the boat later
          // this should be left in, in case something glitches and the boat doesn't come through
          player.teleport(destination);

          System.out.println(">> NO BOAT");
        }
      } else {
        // teleport because sometimes if you're super sneaky (literally) you can teleport
        // to the place you left the shard that you are entering
        player.teleport(destination);

        System.out.println(">> PASSENGER PRIMARY");
        Boat boat = (Boat) destination.getWorld().spawnEntity(destination, EntityType.BOAT);
        boat.setWoodType(TreeSpecies.getByData(boatData.getSpecies()));
        boat.addPassenger(player);

        dao.changeId(boatData.getBoat(), boat.getUniqueId());

        passengerBoatManager.addDriver(boatData.getBoat(), boat);
      }

    } else if (cause == TeleportCause.TO_AN) {
      AquaNether aquaNether = shardManager.getAquaNether();
      destination.setY(aquaNether.getYSpawn());

      player.teleport(destination);
    } else if (cause == TeleportCause.FROM_AN) {
      Location highestBlock = destination.clone();
      getHighestBlock(highestBlock);

      if (highestBlock.getY() < 0) {
        // guess some random "safe blocks", hope we get lucky
        double r = 0;
        Location newLocation = highestBlock.clone();
        while (newLocation.getY() < 0 && r < 100) {
          r++;
          double xm = (4 * r) * Math.random() - (r * 2);
          double zm = (4 * r) * Math.random() - (r * 2);
          newLocation.setX(highestBlock.getX() + xm);
          newLocation.setZ(highestBlock.getZ() + zm);
          getHighestBlock(newLocation);
        }
        highestBlock = newLocation;
      }

      highestBlock.add(0, 1, 0);
      getCentreBlock(highestBlock);
      player.teleport(highestBlock);
    }

    player.getInventory().setContents(new GZIPInventorySerializer().deserialize(packet.getInventorySerial()));
    player.updateInventory();

    player.setVelocity(new Vector());
    player.setFallDistance(0);
    player.setRemainingAir(packet.getAir());
    player.setHealth(packet.getHealth());
    player.setExp(packet.getXp());
    player.setLevel(packet.getLevels());
    player.setExhaustion(packet.getExhaustion());
    player.setSaturation(packet.getSaturation());
    player.setFoodLevel(packet.getFood());
    player.getInventory().setHeldItemSlot(packet.getHotbar());
  }

  private void getCentreBlock(Location loc) {
    loc.setX(loc.getBlockX() + 0.5D);
    loc.setZ(loc.getBlockZ() + 0.5D);
  }

  private void getHighestBlock(Location loc) {
    for (int y = 255; y > -1; y--) {
      loc.setY(y);
      if (loc.getBlock().getType() != Material.AIR) {
        if (loc.getBlock().getType() != Material.LAVA
            && loc.getBlock().getType() != Material.CACTUS) {
          if (y == 0 &&
              loc.getBlock().getType() != Material.BEDROCK) {
            loc.setY(-1.0D);
            return;
          }

          return;
        }
        loc.setY(-1.0D);
        return;
      }
    }
  }
}
