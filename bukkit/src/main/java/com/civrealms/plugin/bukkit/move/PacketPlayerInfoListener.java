package com.civrealms.plugin.bukkit.move;

import com.civrealms.plugin.bukkit.shard.JoinShardManager;
import com.civrealms.plugin.common.packet.PacketReceiveEvent;
import com.civrealms.plugin.common.packets.PacketPlayerInfo;
import com.google.common.eventbus.Subscribe;

public class PacketPlayerInfoListener {
  private final JoinShardManager joinShardManager;
  public PacketPlayerInfoListener(JoinShardManager joinShardManager) {
    this.joinShardManager = joinShardManager;
  }

  @Subscribe
  public void on(PacketReceiveEvent event) {
    if (!(event.getPacket() instanceof PacketPlayerInfo)) {
      return;
    }

    PacketPlayerInfo packet = (PacketPlayerInfo) event.getPacket();
    joinShardManager.addPlayerInfoPacket(packet);
  }
}
