package com.civrealms.plugin.common.packet;

public interface PacketSender {
  default void send(String destination, Packet packet) {
    send(destination, packet, null, null);
  }
  void send(String destination, Packet packet, Runnable success, Runnable fail);
  default void sendProxy(Packet packet) {
    sendProxy(packet, null, null);
  }
  void sendProxy(Packet packet, Runnable success, Runnable fail);
}
