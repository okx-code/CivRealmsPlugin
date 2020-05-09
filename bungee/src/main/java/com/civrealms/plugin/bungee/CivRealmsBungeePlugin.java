package com.civrealms.plugin.bungee;

import com.civrealms.plugin.bungee.message.ProxyMessageListener;
import com.civrealms.plugin.bungee.packet.BungeeDataSender;
import com.civrealms.plugin.bungee.packet.BungeePacketManager;
import com.civrealms.plugin.bungee.playerinfo.PlayerInfoListener;
import com.civrealms.plugin.bungee.playerinfo.PlayerInfoManager;
import com.civrealms.plugin.bungee.shard.PacketIdentifyListener;
import com.civrealms.plugin.bungee.shard.ShardManager;
import com.civrealms.plugin.common.packet.PacketManager;
import com.civrealms.plugin.common.shard.AquaNether;
import com.civrealms.plugin.common.shard.CircleShard;
import com.civrealms.plugin.common.shard.Shard;
import com.google.common.eventbus.EventBus;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.md_5.bungee.api.plugin.Plugin;

public class CivRealmsBungeePlugin extends Plugin {
  private ConfigurationManager config;

  @Override
  public void onEnable() {
    this.config = new ConfigurationManager(getDataFolder());

    getProxy().registerChannel("CR_DATA");

    EventBus bus = new EventBus();

    DestinationManager destinationManager = new DestinationManager();

    PacketManager packetManager = new BungeePacketManager(bus, new BungeeDataSender(destinationManager));

    Set<Shard> shards = new HashSet<>();
//    shards.add(new CircleShard("server2", 0, 0, 100));
      shards.add(new CircleShard("server1", -3600, 600, 100));

    Map<String, AquaNether> aquaNetherMap = new HashMap<>();
    aquaNetherMap.put("server1", new AquaNether(true, 0, 256, "aqua"));
//    // TODO allow aqua nether opposite server to be dynamic
    aquaNetherMap.put("aqua", new AquaNether(false, 253, 251, null));

    ShardManager shardManager = new ShardManager("transitive", shards, aquaNetherMap, packetManager);
    bus.register(new PacketIdentifyListener(packetManager, shardManager));

    bus.register(new PlayerInfoListener(packetManager, new PlayerInfoManager()));

    getProxy().getPluginManager().registerListener(this, new ProxyMessageListener(destinationManager, packetManager));

  }
}
