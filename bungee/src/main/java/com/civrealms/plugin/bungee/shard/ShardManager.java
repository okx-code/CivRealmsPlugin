package com.civrealms.plugin.bungee.shard;

import com.civrealms.plugin.common.packet.PacketSender;
import com.civrealms.plugin.common.shard.AquaNether;
import com.civrealms.plugin.common.shard.Shard;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ShardManager {
  private final String transitiveShard; // the "shard between shards"; the endless ocean
  private final Set<Shard> shards;
  private final Map<String, AquaNether> aquaNetherMap;
  private final PacketSender sender;

  public ShardManager(String transitiveShard, Set<Shard> shards,
      Map<String, AquaNether> aquaNetherMap,
      PacketSender sender) {
    this.transitiveShard = transitiveShard;
    this.shards = shards;
    this.aquaNetherMap = aquaNetherMap;
    this.sender = sender;
  }

  public Set<Shard> getModifiedShards(String server) {
    Set<Shard> shards = new HashSet<>();

    for (Shard shard : this.shards) {
      if (shard.getServer().equalsIgnoreCase(server)) {
        // shards for leaving the server should have their radius extended so they don't overlap
        shards.add(shard.increaseRadius());
      } else {
        shards.add(shard);
      }
    }

    return shards;
  }

  public String getTransitiveShard() {
    return transitiveShard;
  }

  public AquaNether getAquaNether(String server) {
    return aquaNetherMap.get(server.toLowerCase());
  }


  public Set<Shard> getShards() {
    return shards;
  }
}
