package com.civrealms.plugin.common.packet;

import com.civrealms.plugin.common.packets.stream.DataInputStream;
import com.civrealms.plugin.common.packets.stream.DataOutputStream;

public interface Packet {
  void read(DataInputStream in);
  void write(DataOutputStream out);
}
