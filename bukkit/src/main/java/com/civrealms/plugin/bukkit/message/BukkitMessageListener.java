package com.civrealms.plugin.bukkit.message;

import com.civrealms.plugin.common.packet.DataReceiver;
import com.civrealms.plugin.common.packets.stream.DataInputStream;
import com.google.common.io.ByteArrayDataInput;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BukkitMessageListener implements PluginMessageListener {
  private final DataReceiver receiver;

  public BukkitMessageListener(DataReceiver receiver) {
    this.receiver = receiver;
  }

  @Override
  public void onPluginMessageReceived(String channel, Player player, byte[] message) {
    if (channel.equals("BungeeCord")) {
      recieveForwardedMessage(message);
    } else if (channel.equals("CR_DATA")) {
      receiver.receivePacket(message);
    }
  }

  private void recieveForwardedMessage(byte[] data) {
    DataInputStream in = new DataInputStream(data);
    String subchannel = in.readUTF();
    if (subchannel.equals("CR_DATA")) {
      byte[] msgin = unwrapForward(in);

      receiver.receivePacket(msgin);
    }
  }

  private byte[] unwrapForward(ByteArrayDataInput in) {
    short len = in.readShort();
    byte[] msgbytes = new byte[len];
    in.readFully(msgbytes);

    return msgbytes;
  }
}
