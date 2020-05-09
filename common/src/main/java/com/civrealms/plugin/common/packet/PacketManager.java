package com.civrealms.plugin.common.packet;

import com.civrealms.plugin.common.packets.PacketRequestShards;
import com.civrealms.plugin.common.packets.PacketPlayerInfo;
import com.civrealms.plugin.common.packets.PacketRequestPlayer;
import com.civrealms.plugin.common.packets.PacketShardInfo;
import com.civrealms.plugin.common.packets.stream.DataInputStream;
import com.civrealms.plugin.common.packets.stream.DataOutputStream;
import com.google.common.eventbus.EventBus;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class PacketManager implements PacketSender, DataReceiver {

  protected final EventBus bus;
  protected final DataSender sender;
  private final Map<Integer, Constructor<? extends Packet>> packetMap;
  private final Map<Class<? extends Packet>, Integer> packetIdMap;

  public PacketManager(EventBus bus, DataSender sender) {
    this.bus = bus;
    this.sender = sender;
    this.packetMap = new HashMap<>();
    this.packetIdMap = new HashMap<>();

    addPacket(1, PacketRequestShards.class);
    addPacket(2, PacketShardInfo.class);
    addPacket(3, PacketPlayerInfo.class);
    addPacket(4, PacketRequestPlayer.class);
  }

  private void addPacket(int id, Class<? extends Packet> clazz) {
    packetMap.put(id, getConstructor(clazz));
    packetIdMap.put(clazz, id);
  }

  private Constructor<? extends Packet> getConstructor(Class<? extends Packet> clazz) {
    try {
      return clazz.getDeclaredConstructor();
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Constructor not found", e);
    }
  }

  private Packet createPacket(int id) {
    Constructor<? extends Packet> packetConstructor = packetMap.get(id);
    Objects.requireNonNull(packetConstructor, "Unregistered packet id " + id);
    try {
      return packetConstructor.newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }

  protected Packet deserializePacket(byte[] bytes) {
    DataInputStream in = new DataInputStream(bytes);
    int id = in.readInt();

    Packet packet = createPacket(id);
    packet.read(in);

    return packet;
  }

  protected byte[] serializePacket(Packet packet) {
    DataOutputStream out = new DataOutputStream();

    Integer v = packetIdMap.get(packet.getClass());
    if (v == null) {
      throw new IllegalArgumentException("Could not find packet id for " + packet.getClass());
    }
    out.writeInt(v);
    packet.write(out);

    return out.toByteArray();
  }
}
