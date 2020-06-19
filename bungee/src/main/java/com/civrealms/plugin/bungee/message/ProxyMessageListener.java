package com.civrealms.plugin.bungee.message;

import com.civrealms.plugin.common.packet.PacketManager;
import com.civrealms.plugin.common.packets.stream.DataInputStream;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ProxyMessageListener implements Listener {
  private final PacketManager packetManager;

  public ProxyMessageListener(PacketManager packetManager) {
    this.packetManager = packetManager;
  }

  @EventHandler
  public void on(PluginMessageEvent event) {
    if (!event.getTag().equals("CR_DATA")) {
      return;
    }

    System.out.println("RCV P " + event.getReceiver().getClass().getSimpleName() + " S " + event.getSender().getClass().getSimpleName());


    if (event.getReceiver() instanceof ProxiedPlayer) {
      ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
      DataInputStream in = new DataInputStream(event.getData());
      String from = in.readUTF();

      packetManager.receivePacket(event.getData());
    }
  }
}
