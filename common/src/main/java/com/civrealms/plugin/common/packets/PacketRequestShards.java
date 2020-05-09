package com.civrealms.plugin.common.packets;

import com.civrealms.plugin.common.packet.Packet;
import com.civrealms.plugin.common.packets.stream.DataInputStream;
import com.civrealms.plugin.common.packets.stream.DataOutputStream;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PacketRequestShards implements Packet {
  private String name;

  @Override
  public void read(DataInputStream in) {
    this.name = in.readUTF();
  }

  @Override
  public void write(DataOutputStream out) {
    out.writeUTF(name);
  }
}
