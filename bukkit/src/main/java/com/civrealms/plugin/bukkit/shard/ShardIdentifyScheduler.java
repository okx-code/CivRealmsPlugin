
package com.civrealms.plugin.bukkit.shard;

import com.civrealms.plugin.common.packet.PacketManager;

public class ShardIdentifyScheduler implements Runnable {
  private final ShardManager shardManager;
  private final PacketManager packetManager;

  public ShardIdentifyScheduler(ShardManager shardManager, PacketManager packetManager) {
    this.shardManager = shardManager;
    this.packetManager = packetManager;
  }

  @Override
  public void run() {
    shardManager.sendIdentify();
  }
}
