package com.civrealms.plugin.bukkit.message;

import com.civrealms.plugin.bukkit.CivRealmsPlugin;
import com.civrealms.plugin.bukkit.BungeeMessenger;
import com.civrealms.plugin.common.packet.DataSender;
import com.civrealms.plugin.common.packets.stream.DataOutputStream;
import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitMessenger implements BungeeMessenger, DataSender {

  private final CivRealmsPlugin plugin;

  public BukkitMessenger(CivRealmsPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void connect(Player player, String server) {
    DataOutputStream out = new DataOutputStream();
    out.writeUTF("Connect");
    out.writeUTF(server);

    player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
  }

  @Override
  public void send(String destination, byte[] bytes) {
    DataOutputStream out = new DataOutputStream();
    out.write(bytes);
    sendPluginMessage("BungeeCord", wrapForward(destination, "CR_DATA", out).toByteArray());
  }

  @Override
  public void sendProxy(byte[] data) {
    sendPluginMessage("CR_DATA", data);
  }

  private DataOutputStream wrapForward(String server, String subchannel, DataOutputStream data) {
    DataOutputStream out = new DataOutputStream();
    out.writeUTF("Forward");
    out.writeUTF(server);
    out.writeUTF(subchannel);

    byte[] bytes = data.toByteArray();
    out.writeShort(bytes.length);
    out.write(bytes);

    return out;
  }

  private void sendPluginMessage(String channel, byte[] bytes) {
    Player player = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
    if (player == null) {
      return;
    }

    player.sendPluginMessage(plugin, channel, bytes);
  }
}
