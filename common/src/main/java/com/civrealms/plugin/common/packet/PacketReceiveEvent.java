package com.civrealms.plugin.common.packet;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
public class PacketReceiveEvent {
  @Setter
  private boolean acknowledged = true;
  private final boolean fromProxy;
  private final String server;
  private final Packet packet;

  public static PacketReceiveEvent receivedFromProxy(Packet packet) {
    return new PacketReceiveEvent(true, null, packet);
  }

  public static PacketReceiveEvent receivedFromBukkit(String server, Packet packet) {
    return new PacketReceiveEvent(false, server, packet);
  }

  public void reply(PacketManager packetManager, Packet reply) {
    if (fromProxy) {
      packetManager.sendProxy(reply);
    } else {
      packetManager.send(server, reply);
    }
  }
}
