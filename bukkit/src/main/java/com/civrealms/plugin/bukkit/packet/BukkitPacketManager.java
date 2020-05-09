package com.civrealms.plugin.bukkit.packet;

import com.civrealms.plugin.common.packet.DataSender;
import com.civrealms.plugin.common.packet.Packet;
import com.civrealms.plugin.common.packet.PacketManager;
import com.civrealms.plugin.common.packet.PacketReceiveEvent;
import com.civrealms.plugin.common.packets.stream.DataInputStream;
import com.civrealms.plugin.common.packets.stream.DataOutputStream;
import com.google.common.eventbus.EventBus;

public class BukkitPacketManager extends PacketManager {

  private final String server;

  public BukkitPacketManager(String server, EventBus bus, DataSender sender) {
    super(bus, sender);
    this.server = server;
  }

  @Override
  public void receivePacket(byte[] data) {
    DataInputStream in = new DataInputStream(data);

    boolean fromProxy = in.readBoolean();
    if (fromProxy) {
      Packet packet = deserializePacket(in.readByteArray());
      bus.post(PacketReceiveEvent.receivedFromProxy(packet));
    } else {
      String from = in.readUTF();
      Packet packet = deserializePacket(in.readByteArray());
      bus.post(PacketReceiveEvent.receivedFromServer(from, packet));
    }
  }

  @Override
  public void send(String destination, Packet packet) {
    DataOutputStream out = new DataOutputStream();

    out.writeBoolean(false);
    out.writeUTF(server);

    out.writeByteArray(serializePacket(packet));

    sender.send(destination, out.toByteArray());
  }

  @Override
  public void sendProxy(Packet packet) {
    DataOutputStream out = new DataOutputStream();

    out.writeUTF(server);
    out.writeByteArray(serializePacket(packet));

    sender.sendProxy(out.toByteArray());
  }
}
