package com.civrealms.plugin.bukkit;

import com.civrealms.plugin.bukkit.boat.BoatInventoryCommand;
import com.civrealms.plugin.bukkit.boat.BoatInventoryDao;
import com.civrealms.plugin.bukkit.boat.BoatInventoryListener;
import com.civrealms.plugin.bukkit.boat.MySqlBoatInventoryDao;
import com.civrealms.plugin.bukkit.message.BukkitMessageListener;
import com.civrealms.plugin.bukkit.message.BukkitMessenger;
import com.civrealms.plugin.bukkit.move.AquaNetherMoveListener;
import com.civrealms.plugin.bukkit.move.PacketPlayerInfoListener;
import com.civrealms.plugin.bukkit.move.ShardMoveListener;
import com.civrealms.plugin.bukkit.packet.BukkitPacketManager;
import com.civrealms.plugin.bukkit.shard.JoinListener;
import com.civrealms.plugin.bukkit.shard.LeaveShardManager;
import com.civrealms.plugin.bukkit.shard.ShardIdentifyScheduler;
import com.civrealms.plugin.bukkit.shard.ShardManager;
import com.civrealms.plugin.common.packet.PacketManager;
import com.google.common.eventbus.EventBus;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CivRealmsPlugin extends JavaPlugin {

  private EventBus bus;

  private BukkitMessenger messenger;
  private ShardManager shardManager;
  private PacketManager packetManager;

  @Override
  public void onEnable() {
    new Tick().runTaskTimer(this, 1, 1);

    saveDefaultConfig();

    String shard = getConfig().getString("shard");

    this.bus = new EventBus();

    this.messenger = new BukkitMessenger(this);
    this.packetManager = new BukkitPacketManager(shard, bus, messenger);

    LeaveShardManager leaveShard = new LeaveShardManager();

    BukkitMessageListener messageListener = new BukkitMessageListener(packetManager);
    org.bukkit.plugin.messaging.Messenger messenger = this.getServer().getMessenger();
    messenger.registerIncomingPluginChannel(this, "BungeeCord", messageListener);
    messenger.registerIncomingPluginChannel(this, "CR_DATA", messageListener);
    messenger.registerOutgoingPluginChannel(this, "BungeeCord");
    messenger.registerOutgoingPluginChannel(this, "CR_DATA");

    this.shardManager = new ShardManager(this, shard, this.messenger, packetManager, leaveShard);
    bus.register(shardManager);

    Bukkit.getScheduler().runTaskTimer(this, new ShardIdentifyScheduler(shardManager, packetManager), 1, 4000);

    getServer().getPluginManager().registerEvents(new ShardMoveListener(this, shardManager, leaveShard), this);
    getServer().getPluginManager().registerEvents(new JoinListener(this, shardManager, packetManager), this);
    getServer().getPluginManager().registerEvents(new AquaNetherMoveListener(shardManager), this);
    getServer().getPluginManager().registerEvents(leaveShard, this);

    BoatInventoryDao dao = new MySqlBoatInventoryDao("localhost", 3306, "civrealms", "root", "");
    getCommand("boatinventory").setExecutor(new BoatInventoryCommand(dao));
    getServer().getPluginManager().registerEvents(new BoatInventoryListener(dao), this);

    bus.register(new PacketPlayerInfoListener(this, shardManager, dao));
  }
}
