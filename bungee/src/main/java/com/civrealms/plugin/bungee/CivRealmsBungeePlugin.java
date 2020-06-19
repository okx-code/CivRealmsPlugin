package com.civrealms.plugin.bungee;

import com.civrealms.plugin.bungee.packet.BungeePacketManager;
import com.civrealms.plugin.bungee.shard.PacketIdentifyListener;
import com.civrealms.plugin.bungee.shard.ShardManager;
import com.civrealms.plugin.common.packet.PacketManager;
import com.civrealms.plugin.common.rabbit.RabbitClient;
import com.civrealms.plugin.common.rabbit.RabbitPacketListener;
import com.civrealms.plugin.common.rabbit.RabbitSender;
import com.civrealms.plugin.common.shard.AquaNether;
import com.civrealms.plugin.common.shard.CircleShard;
import com.civrealms.plugin.common.shard.Shard;
import com.google.common.eventbus.EventBus;
import com.rabbitmq.client.Channel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import net.md_5.bungee.api.plugin.Plugin;

public class CivRealmsBungeePlugin extends Plugin {
  private ConfigurationManager config;

  @Override
  public void onEnable() {
    this.config = new ConfigurationManager(getDataFolder());

    getProxy().registerChannel("CR_DATA");

    EventBus bus = new EventBus();

    Supplier<Channel> channels = new RabbitClient().getChannels();
    RabbitSender sender = new RabbitSender(channels.get());

    PacketManager packetManager = new BungeePacketManager(bus, sender);

    RabbitPacketListener listener = new RabbitPacketListener("proxy", packetManager, channels.get());

    Set<Shard> shards = new HashSet<>();
//    shards.add(new CircleShard("server2", 0, 0, 100));
      shards.add(new CircleShard("server1", -3600, 600, 100));

    Map<String, AquaNether> aquaNetherMap = new HashMap<>();
    aquaNetherMap.put("server1", new AquaNether(true, 0, 256, "aqua", 63));
    aquaNetherMap.put("transitive", new AquaNether(true, 0, 256, "aqua", 63));
    aquaNetherMap.put("aqua", new AquaNether(false, 253, 251, null, 63));

    ShardManager shardManager = new ShardManager("transitive", shards, aquaNetherMap, packetManager);
    bus.register(new PacketIdentifyListener(packetManager, shardManager));

//    getProxy().getPluginManager().registerListener(this, new ProxyMessageListener(destinationManager, packetManager));

  }
}
