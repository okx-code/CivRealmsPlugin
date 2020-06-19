package com.civrealms.plugin.common.packet;

public interface DataSender {
  default void send(String destination, byte[] bytes) {
    send(destination, bytes, null, null);
  }
  void send(String destination, byte[] bytes, Runnable success, Runnable fail);
  default void sendProxy(byte[] bytes) {
    sendProxy(bytes, null, null);
  }
  void sendProxy(byte[] bytes, Runnable success, Runnable fail);
}
