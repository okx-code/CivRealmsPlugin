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
import net.md_5.bungee.config.Configuration;

public class CivRealmsBungeePlugin extends Plugin {
  private ConfigurationManager config;

  @Override
  public void onEnable() {
    this.config = new ConfigurationManager(getDataFolder());
    Configuration cc = config.get("config.yml");

    getProxy().registerChannel("CR_DATA");

    EventBus bus = new EventBus();

    Supplier<Channel> channels = new RabbitClient().getChannels();
    RabbitSender sender = new RabbitSender(channels.get());

    PacketManager packetManager = new BungeePacketManager(bus, sender);

    RabbitPacketListener listener = new RabbitPacketListener("proxy", packetManager, channels.get());

    Set<Shard> shards = new HashSet<>();
    Map<String, AquaNether> aquaNetherMap = new HashMap<>();

    Configuration shardsConfig = cc.getSection("shards");
    for (String key : shardsConfig.getKeys()) {
      Configuration shard = shardsConfig.getSection(key);
      shards.add(new CircleShard(key, shard.getInt("centre-x"), shard.getInt("centre-z"), shard.getInt("radius")));
      aquaNetherMap.put(key, new AquaNether(true, 0, 256, shard.getString("aqua-nether.server"), shard.getInt("aqua-nether.ocean-height")));
    }

    Configuration transitiveConfig = cc.getSection("transitive");
    String deathShard = transitiveConfig.getString("death-shard");
    aquaNetherMap.put("transitive", new AquaNether(true, 0, 256, transitiveConfig.getString("aqua-nether.server"), transitiveConfig.getInt("aqua-nether.ocena-height")));

    Configuration aquaNetherConfig = cc.getSection("aqua-nether");
    aquaNetherMap.put("aqua", new AquaNether(false, aquaNetherConfig.getInt("y-teleport"), aquaNetherConfig.getInt("y-spawn"), null, 63));

    ShardManager shardManager = new ShardManager("transitive", deathShard, shards, aquaNetherMap, packetManager);
    bus.register(new PacketIdentifyListener(packetManager, shardManager));

//    getProxy().getPluginManager().registerListener(this, new ProxyMessageListener(destinationManager, packetManager));

  }
}
