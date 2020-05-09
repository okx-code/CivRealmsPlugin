package com.civrealms.plugin.bukkit.shard;

import com.civrealms.plugin.bukkit.BungeeMessenger;
import com.civrealms.plugin.bukkit.inventory.GZIPInventorySerializer;
import com.civrealms.plugin.common.packet.PacketReceiveEvent;
import com.civrealms.plugin.common.packet.PacketSender;
import com.civrealms.plugin.common.packets.PacketRequestShards;
import com.civrealms.plugin.common.packets.PacketPlayerInfo;
import com.civrealms.plugin.common.packets.PacketShardInfo;
import com.civrealms.plugin.common.packets.data.BoatData;
import com.civrealms.plugin.common.shard.AquaNether;
import com.civrealms.plugin.common.shard.Shard;
import com.google.common.eventbus.Subscribe;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class ShardManager {
  private final Plugin scheduler;
  private final String currentShard;
  private final BungeeMessenger messenger;
  private final PacketSender sender;
  private final LeaveShardManager leaveShardManager;
  private String transitiveShard;
  private AquaNether aquaNether;
  private Collection<Shard> shards;

  public ShardManager(Plugin scheduler, String currentShard, BungeeMessenger messenger,
      PacketSender sender, LeaveShardManager leaveShardManager) {
    this.scheduler = scheduler;
    this.currentShard = currentShard;
    this.messenger = messenger;
    this.sender = sender;
    this.leaveShardManager = leaveShardManager;
  }

  public void sendIdentify() {
    System.out.println("IDENTIFYING TO PROXY AS >> " + currentShard);
    sender.sendProxy(new PacketRequestShards(currentShard));
  }

  private void readShards(PacketShardInfo packet) {
    this.transitiveShard = packet.getTransitiveShard();
    this.aquaNether = packet.getAquaNether();
    this.shards = new HashSet<>(packet.getShards());
  }

  public void sendPlayer(Player player, PacketPlayerInfo.TeleportCause cause, BoatData boat, String server) {
    leaveShardManager.setLeaving(player);

    // Send to proxy to store so it can do something later
    // we can't send a message directly to the other server because
    // of the risk that it won't have any players online -
    // servers with no players cannot send nor receive plugin messages
    Location loc = player.getLocation();
    sender.sendProxy(new PacketPlayerInfo(
        cause,
        boat,
        player.getUniqueId(),
        new GZIPInventorySerializer().serialize(player.getInventory().getContents()),
        loc.getX(),
        loc.getY(),
        loc.getZ(),
        loc.getYaw(),
        loc.getPitch(),
        player.getRemainingAir(),
        player.getHealth(),
        player.getExp(),
        player.getLevel(),
        player.getExhaustion(),
        player.getSaturation(),
        player.getFoodLevel(),
        player.getInventory().getHeldItemSlot()
    ));

    // and connect the player
    messenger.connect(player, server);

    // clear inventory
    player.getInventory().setContents(new ItemStack[player.getInventory().getContents().length]);
  }

  @Subscribe
  public void onShardsReceive(PacketReceiveEvent e) {
    if (!(e.getPacket() instanceof PacketShardInfo)) {
      return;
    }
    PacketShardInfo packet = (PacketShardInfo) e.getPacket();

    System.out.println("READ SHARDS # >> " + packet.getShards().size());
    readShards(packet);
  }

  public String getCurrentShard() {
    return currentShard;
  }

  public Collection<Shard> getShards() {
    return shards;
  }

  public AquaNether getAquaNether() {
    return aquaNether;
  }

  public String getTransitiveShard() {
    return transitiveShard;
  }

  public String getShard(int x, int y, int z) {
    if (shards == null || shards.isEmpty()) {
      return null;
    }
    Shard destShard = null;
    for (Shard shard : shards) {
      if (shard.contains(x, y, z)) {
        destShard = shard;
        break;
      }
    }

    if (destShard == null) {
      return transitiveShard;
    } else {
      return destShard.getServer();
    }
  }
}
