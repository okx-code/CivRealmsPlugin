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
  public boolean receivePacket(byte[] data) {
    DataInputStream in = new DataInputStream(data);

    boolean fromProxy = in.readBoolean();
    PacketReceiveEvent event;
    if (fromProxy) {
      Packet packet = deserializePacket(in.readByteArray());
      System.out.println("IN PROXY >> " + packet.getClass().getSimpleName());
      event = PacketReceiveEvent.receivedFromProxy(packet);
    } else {
      String from = in.readUTF();
      Packet packet = deserializePacket(in.readByteArray());
      System.out.println("IN BUKKIT >> " + packet.getClass().getSimpleName());
      event = PacketReceiveEvent.receivedFromBukkit(from, packet);
    }
    bus.post(event);
    return event.isAcknowledged();
  }

  @Override
  public void send(String destination, Packet packet, Runnable success, Runnable fail) {
    System.out.println("OUT BUKKIT >> " + destination + " >> " + packet.getClass().getSimpleName());

    DataOutputStream out = new DataOutputStream();

    out.writeBoolean(false);
    out.writeUTF(server);

    byte[] b = serializePacket(packet);
    System.out.println("SERIALIZED AS " + b.length + " BYTES");
    out.writeByteArray(b);

    System.out.println("OUT >>> " + out.toByteArray().length);

    sender.send(destination, out.toByteArray(), success, fail);
  }

  @Override
  public void sendProxy(Packet packet, Runnable success, Runnable fail) {
    System.out.println("OUT PROXY >> " + packet.getClass().getSimpleName());

    DataOutputStream out = new DataOutputStream();

    out.writeUTF(server);
    out.writeByteArray(serializePacket(packet));

    sender.sendProxy(out.toByteArray(), success, fail);
  }
}
