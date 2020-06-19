package com.civrealms.plugin.bukkit.move;

import com.civrealms.plugin.bukkit.shard.JoinShardManager;
import com.civrealms.plugin.common.packet.PacketReceiveEvent;
import com.civrealms.plugin.common.packets.PacketPlayerTransfer;
import com.google.common.eventbus.Subscribe;

public class PacketPlayerInfoListener {
  private final JoinShardManager joinShardManager;
  public PacketPlayerInfoListener(JoinShardManager joinShardManager) {
    this.joinShardManager = joinShardManager;
  }

  @Subscribe
  public void on(PacketReceiveEvent event) {
    if (!(event.getPacket() instanceof PacketPlayerTransfer)) {
      return;
    }

    PacketPlayerTransfer packet = (PacketPlayerTransfer) event.getPacket();
    joinShardManager.addPlayerInfoPacket(packet);
  }
}
