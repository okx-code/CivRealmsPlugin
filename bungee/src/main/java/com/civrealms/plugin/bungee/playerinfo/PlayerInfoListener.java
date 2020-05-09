package com.civrealms.plugin.bungee.playerinfo;

import com.civrealms.plugin.common.packet.PacketManager;
import com.civrealms.plugin.common.packet.PacketReceiveEvent;
import com.civrealms.plugin.common.packets.PacketPlayerInfo;
import com.civrealms.plugin.common.packets.PacketRequestPlayer;
import com.google.common.eventbus.Subscribe;

public class PlayerInfoListener {
  private final PacketManager packetManager;
  private final PlayerInfoManager manager;

  public PlayerInfoListener(PacketManager packetManager,
      PlayerInfoManager manager) {
    this.packetManager = packetManager;
    this.manager = manager;
  }

  @Subscribe
  public void onPlayerInfo(PacketReceiveEvent event) {
    if (!(event.getPacket() instanceof PacketPlayerInfo)) {
      return;
    }

    PacketPlayerInfo packet = (PacketPlayerInfo) event.getPacket();
    manager.addPacket(packet);

    System.out.println("STORING DATA >> " + packet.getUuid());
  }

  @Subscribe
  public void onRequestInfo(PacketReceiveEvent event) {
    if (!(event.getPacket() instanceof PacketRequestPlayer)) {
      return;
    }

    PacketRequestPlayer packet = (PacketRequestPlayer) event.getPacket();
    PacketPlayerInfo reply = manager.getPacket(packet.getUuid());
    if (reply != null) {
      System.out.println("RETRIEVING DATA >> " + packet.getUuid() + " > TO > " + event.getServer());
      event.reply(packetManager, reply);
    }
  }
}
