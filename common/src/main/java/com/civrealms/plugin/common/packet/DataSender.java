package com.civrealms.plugin.common.packet;

public interface DataSender {
  void send(String destination, byte[] bytes);
  void sendProxy(byte[] bytes);
}
