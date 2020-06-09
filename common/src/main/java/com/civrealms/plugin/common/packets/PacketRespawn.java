package com.civrealms.plugin.common.packets;

import com.civrealms.plugin.common.packet.Packet;
import com.civrealms.plugin.common.packets.stream.DataInputStream;
import com.civrealms.plugin.common.packets.stream.DataOutputStream;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents a player dying and respawning on possibly a different server
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PacketRespawn implements Packet {
  private UUID uuid;

  @Override
  public void read(DataInputStream in) {
    uuid = in.readUUID();
  }

  @Override
  public void write(DataOutputStream out) {
    out.writeUUID(uuid);
  }
}
