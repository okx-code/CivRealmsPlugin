package com.civrealms.plugin.bukkit;

import com.civrealms.plugin.bukkit.boat.BoatInventoryCommand;
import com.civrealms.plugin.bukkit.boat.BoatInventoryDao;
import com.civrealms.plugin.bukkit.boat.BoatInventoryListener;
import com.civrealms.plugin.bukkit.boat.InventoryOpenListener;
import com.civrealms.plugin.bukkit.boat.MySqlBoatInventoryDao;
import com.civrealms.plugin.bukkit.inventory.log.InventoryLogDao;
import com.civrealms.plugin.bukkit.inventory.log.InventoryLogger;
import com.civrealms.plugin.bukkit.inventory.log.MySqlInventoryLogDao;
import com.civrealms.plugin.bukkit.message.BukkitMessenger;
import com.civrealms.plugin.bukkit.move.AquaNetherMoveListener;
import com.civrealms.plugin.bukkit.move.PacketPlayerInfoListener;
import com.civrealms.plugin.bukkit.move.ShardMoveListener;
import com.civrealms.plugin.bukkit.move.VoidDamageListener;
import com.civrealms.plugin.bukkit.packet.BukkitPacketManager;
import com.civrealms.plugin.bukkit.respawn.BukkitRandomSpawn;
import com.civrealms.plugin.bukkit.respawn.WrapperBukkitRandomSpawn;
import com.civrealms.plugin.bukkit.respawn.DummyRandomSpawn;
import com.civrealms.plugin.bukkit.respawn.PlayerRespawnListener;
import com.civrealms.plugin.bukkit.shard.JoinListener;
import com.civrealms.plugin.bukkit.shard.JoinShardManager;
import com.civrealms.plugin.bukkit.shard.LeaveShardManager;
import com.civrealms.plugin.bukkit.shard.ShardIdentifyScheduler;
import com.civrealms.plugin.bukkit.shard.ShardManager;
import com.civrealms.plugin.common.packet.PacketManager;
import com.civrealms.plugin.common.rabbit.RabbitClient;
import com.civrealms.plugin.common.rabbit.RabbitPacketListener;
import com.civrealms.plugin.common.rabbit.RabbitSender;
import com.google.common.eventbus.EventBus;
import com.rabbitmq.client.Channel;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class CivRealmsPlugin extends JavaPlugin {

  private EventBus bus;

  private ShardManager shardManager;
  private PacketManager packetManager;

  @Override
  public void onEnable() {
    getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    new Tick().runTaskTimer(this, 1, 1);

    saveDefaultConfig();

    String shard = getConfig().getString("shard");

    // TODO no-op inventory log dao if sql cannot be loaded
    InventoryLogDao logDao = new MySqlInventoryLogDao("localhost", 3306, "civrealms", "root", "");
    InventoryLogger invLogger = new InventoryLogger(this, shard, getLogger(), logDao);

    BukkitRandomSpawn randomSpawn = new WrapperBukkitRandomSpawn(new DummyRandomSpawn());

    this.bus = new EventBus();

    Supplier<Channel> channels = new RabbitClient().getChannels();
    RabbitSender sender = new RabbitSender(channels.get());

    this.packetManager = new BukkitPacketManager(shard, bus, sender);

    RabbitPacketListener listener = new RabbitPacketListener(shard, packetManager, channels.get());

    LeaveShardManager leaveShard = new LeaveShardManager();

    this.shardManager = new ShardManager(this, invLogger, shard, new BukkitMessenger(this), packetManager, leaveShard);
    bus.register(shardManager);

    BoatInventoryDao dao = new MySqlBoatInventoryDao("localhost", 3306, "civrealms", "root", "");

    JoinShardManager joinShardManager = new JoinShardManager(this, shardManager, dao, randomSpawn);
    bus.register(joinShardManager);

    Bukkit.getScheduler().runTaskTimer(this, new ShardIdentifyScheduler(shardManager), 1, 4000);

    getServer().getPluginManager().registerEvents(new ShardMoveListener(this, shardManager, leaveShard, joinShardManager), this);
    getServer().getPluginManager().registerEvents(new JoinListener(invLogger, joinShardManager), this);
    getServer().getPluginManager().registerEvents(new AquaNetherMoveListener(shardManager, leaveShard, joinShardManager), this);
    getServer().getPluginManager().registerEvents(leaveShard, this);
    getServer().getPluginManager().registerEvents(new VoidDamageListener(), this);
    getServer().getPluginManager().registerEvents(new PlayerRespawnListener(this, invLogger, shardManager,
        randomSpawn), this);

    getCommand("boatinventory").setExecutor(new BoatInventoryCommand(dao));
    getServer().getPluginManager().registerEvents(new BoatInventoryListener(dao), this);

    bus.register(new PacketPlayerInfoListener(joinShardManager));

    getServer().getPluginManager().registerEvents(new InventoryOpenListener(), this);
  }
}
