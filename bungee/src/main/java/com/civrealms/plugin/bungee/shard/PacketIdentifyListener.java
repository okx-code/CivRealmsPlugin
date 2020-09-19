package com.civrealms.plugin.bungee.shard;

import com.civrealms.plugin.common.packet.PacketManager;
import com.civrealms.plugin.common.packet.PacketReceiveEvent;
import com.civrealms.plugin.common.packets.PacketRequestShards;
import com.civrealms.plugin.common.packets.PacketShardInfo;
import com.google.common.eventbus.Subscribe;

public class PacketIdentifyListener {
  private final PacketManager packetManager;
  private final ShardManager shardManager;

  public PacketIdentifyListener(PacketManager packetManager,
      ShardManager shardManager) {
    this.packetManager = packetManager;
    this.shardManager = shardManager;
  }

  @Subscribe
  public void on(PacketReceiveEvent event) {
    if (!(event.getPacket() instanceof PacketRequestShards)) {
      return;
    }

    PacketRequestShards identify = (PacketRequestShards) event.getPacket();

    String server = identify.getName();
    System.out.println("IDENTIFIED >> " + server);

//    shardManager.identify(server, );

    PacketShardInfo shardInfo = new PacketShardInfo(shardManager.getTransitiveShard(), shardManager.getDeathShard(), shardManager.getAquaNether(server), shardManager.getModifiedShards(server));

    event.reply(packetManager, shardInfo);
  }

}
