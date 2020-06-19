package com.civrealms.plugin.bukkit.shard;

import com.civrealms.plugin.bukkit.Tick;
import com.civrealms.plugin.bukkit.boat.BoatInventoryDao;
import com.civrealms.plugin.bukkit.inventory.GZIPInventorySerializer;
import com.civrealms.plugin.bukkit.move.PassengerBoatManager;
import com.civrealms.plugin.bukkit.respawn.BukkitRandomSpawn;
import com.civrealms.plugin.common.packet.PacketReceiveEvent;
import com.civrealms.plugin.common.packets.PacketPlayerTransfer;
import com.civrealms.plugin.common.packets.PacketPlayerTransfer.TeleportCause;
import com.civrealms.plugin.common.packets.PacketShardInfo;
import com.civrealms.plugin.common.packets.data.BoatData;
import com.civrealms.plugin.common.shard.AquaNether;
import com.google.common.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.entity.Boat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class JoinShardManager {
  private final Plugin plugin;
  private final ShardManager shardManager;
  private final BoatInventoryDao dao;
  private final BukkitRandomSpawn randomSpawn;
  private final PassengerBoatManager passengerBoatManager = new PassengerBoatManager();
  private final List<PacketPlayerTransfer> joinQueue = new ArrayList<>();
  private final Map<UUID, Integer> joinTimes = new HashMap<>();

  public JoinShardManager(Plugin plugin, ShardManager shardManager,
      BoatInventoryDao dao, BukkitRandomSpawn randomSpawn) {
    this.plugin = plugin;
    this.shardManager = shardManager;
    this.dao = dao;
    this.randomSpawn = randomSpawn;
  }

  void joined(Player player) {
    joinTimes.put(player.getUniqueId(), Tick.get());
  }

  void left(Player player) {
    joinTimes.remove(player.getUniqueId());
  }

  public void checkJoin() {
    Iterator<PacketPlayerTransfer> it = joinQueue.iterator();
    while (it.hasNext()) {
      PacketPlayerTransfer packet = it.next();
      if (isReady(packet)) {
        handlePacket(packet);
        it.remove();
      }
    }
  }

  public boolean isJoining(UUID uuid) {
//    System.out.println("is joining? " + uuid);
    if (joinTimes.containsKey(uuid)) {
      int tickJoined = joinTimes.get(uuid);
//      System.out.println(Tick.get() + " - " + tickJoined + " = " + (Tick.get() - tickJoined));
      // if joined in last 5 seconds
      if (Tick.get() - tickJoined < 100) {
        return true;
      }
    }
    for (PacketPlayerTransfer packet : joinQueue) {
      if (packet.getUniqueId().equals(uuid)) {
//        System.out.println("in join queue");
        return true;
      }
    }
//    System.out.println("not in join queue");
    return false;
  }

  public void addPlayerInfoPacket(PacketPlayerTransfer packet) {
    if (isReady(packet)) {
      handlePacket(packet);
    } else {
      joinQueue.add(packet);
    }
  }

  private boolean isReady(PacketPlayerTransfer packet) {
    if (!shardManager.hasShardInfo()) {
      return false;
    }
    Player player = Bukkit.getPlayer(packet.getUniqueId());
    if (player == null) {
      return false;
    }

    return true;
  }

  @Subscribe
  public void onShardsReceive(PacketReceiveEvent e) {
    if (!(e.getPacket() instanceof PacketShardInfo)) {
      return;
    }
    Bukkit.getScheduler().runTask(plugin, this::checkJoin);
  }

  private void handlePacket(PacketPlayerTransfer packet) {
    Player player = Bukkit.getPlayer(packet.getUniqueId());
    if (player == null) {
      throw new IllegalArgumentException("Could not find player for UUID " + packet.getUniqueId());
    }

    TeleportCause cause = packet.getCause();
    System.out.println("GOT DATA >> " + player.getName() + " >> " + cause);
    Location destination = new Location(player.getWorld(), packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch());
    if (cause == TeleportCause.TRANSITIVE) {
      // adjust for ocean height
      destination.setY(destination.getY() + shardManager.getAquaNether().getOceanHeight());

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
    } else if (cause == TeleportCause.DEATH) {
      if (player.getBedSpawnLocation() != null) {
        player.teleport(player.getBedSpawnLocation());
      } else {
        player.teleport(randomSpawn.getRandomSpawn(player));
      }

      player.getInventory().setContents(new ItemStack[player.getInventory().getContents().length]);
      player.setVelocity(new Vector());
      player.setFallDistance(0);
      player.setHealth(20);
      player.setExp(0);
      player.setLevel(0);
      player.setExhaustion(0);
      player.setSaturation(0);
      player.setFoodLevel(20);
      player.getInventory().setHeldItemSlot(0);
      return;
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
        if (loc.getBlock().getType() != Material.LAVA && loc.getBlock().getType() != Material.CACTUS) {
          if (y == 0 && loc.getBlock().getType() != Material.BEDROCK) {
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
