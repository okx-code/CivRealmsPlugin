package com.civrealms.plugin.common.packets;

import com.civrealms.plugin.common.packet.Packet;
import com.civrealms.plugin.common.packets.stream.DataInputStream;
import com.civrealms.plugin.common.packets.stream.DataOutputStream;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Sent when a player joins and the server asks the proxy if it should do anything about it
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PacketRequestPlayer implements Packet {
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
