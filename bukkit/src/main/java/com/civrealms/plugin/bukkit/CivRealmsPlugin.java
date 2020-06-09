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
import com.civrealms.plugin.bukkit.move.VoidDamageListener;
import com.civrealms.plugin.bukkit.packet.BukkitPacketManager;
import com.civrealms.plugin.bukkit.respawn.DummyRandomSpawn;
import com.civrealms.plugin.bukkit.respawn.PlayerRespawnListener;
import com.civrealms.plugin.bukkit.shard.JoinListener;
import com.civrealms.plugin.bukkit.shard.JoinShardManager;
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
    org.bukkit.plugin.messaging.Messenger pluginMessenger = this.getServer().getMessenger();
    pluginMessenger.registerIncomingPluginChannel(this, "BungeeCord", messageListener);
    pluginMessenger.registerIncomingPluginChannel(this, "CR_DATA", messageListener);
    pluginMessenger.registerOutgoingPluginChannel(this, "BungeeCord");
    pluginMessenger.registerOutgoingPluginChannel(this, "CR_DATA");

    this.shardManager = new ShardManager(this, shard, messenger, packetManager, leaveShard);
    bus.register(shardManager);

    BoatInventoryDao dao = new MySqlBoatInventoryDao("localhost", 3306, "civrealms", "root", "");

    JoinShardManager joinShardManager = new JoinShardManager(this, shardManager, dao, new DummyRandomSpawn());
    bus.register(joinShardManager);

    Bukkit.getScheduler().runTaskTimer(this, new ShardIdentifyScheduler(shardManager), 1, 4000);

    getServer().getPluginManager().registerEvents(new ShardMoveListener(this, shardManager, leaveShard, joinShardManager), this);
    getServer().getPluginManager().registerEvents(new JoinListener(this, shardManager, joinShardManager), this);
    getServer().getPluginManager().registerEvents(new AquaNetherMoveListener(shardManager, leaveShard, joinShardManager), this);
    getServer().getPluginManager().registerEvents(leaveShard, this);
    getServer().getPluginManager().registerEvents(new VoidDamageListener(), this);
    getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this, shardManager), this);

    getCommand("boatinventory").setExecutor(new BoatInventoryCommand(dao));
    getServer().getPluginManager().registerEvents(new BoatInventoryListener(dao), this);

    bus.register(new PacketPlayerInfoListener(joinShardManager));

    getServer().getPluginManager().registerEvents(new testlistener(), this);
  }
}
