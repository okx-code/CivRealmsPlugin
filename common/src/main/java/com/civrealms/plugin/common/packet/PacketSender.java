package com.civrealms.plugin.common.packet;

public interface PacketSender {
  void send(String destination, Packet packet);
  void sendProxy(Packet packet);
}
