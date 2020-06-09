package com.civrealms.plugin.bungee.packet;

import com.civrealms.plugin.common.packet.DataSender;
import com.civrealms.plugin.common.packet.Packet;
import com.civrealms.plugin.common.packet.PacketManager;
import com.civrealms.plugin.common.packet.PacketReceiveEvent;
import com.civrealms.plugin.common.packets.stream.DataInputStream;
import com.civrealms.plugin.common.packets.stream.DataOutputStream;
import com.google.common.eventbus.EventBus;

public class BungeePacketManager extends PacketManager {

  public BungeePacketManager(EventBus bus, DataSender sender) {
    super(bus, sender);
  }

  @Override
  public void send(String destination, Packet packet) {
    DataOutputStream out = new DataOutputStream();
    out.writeBoolean(true);

    out.writeByteArray(serializePacket(packet));
    sender.send(destination, out.toByteArray());
  }

  @Override
  public void sendProxy(Packet packet) {
    throw new UnsupportedOperationException("Cannot send message from proxy to proxy");
  }

  @Override
  public void receivePacket(byte[] data) {
    DataInputStream in = new DataInputStream(data);
    String from = in.readUTF();

    Packet packet = deserializePacket(in.readByteArray());
    bus.post(PacketReceiveEvent.receivedFromBukkit(from, packet));
  }
}
