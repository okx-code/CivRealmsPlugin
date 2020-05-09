package com.civrealms.plugin.bungee.playerinfo;

import com.civrealms.plugin.common.packets.PacketPlayerInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerInfoManager {
  private final Map<UUID, PacketPlayerInfo> info;

  public PlayerInfoManager() {
    this.info = new HashMap<>();
  }

  public void addPacket(PacketPlayerInfo packet) {
    info.put(packet.getUuid(), packet);
  }

  public PacketPlayerInfo getPacket(UUID uuid) {
    return info.remove(uuid);
  }
}
