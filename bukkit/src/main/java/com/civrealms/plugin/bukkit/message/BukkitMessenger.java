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
    System.out.println("CONN >> " + player.getName() + " to '" + server + "'");
    out.writeUTF(server);

    player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
  }

  @Override
  public void send(String destination, byte[] bytes) {
    sendPluginMessage("BungeeCord", wrapForward(destination, "CR_DATA", bytes).toByteArray());
  }

  @Override
  public void sendProxy(byte[] data) {
    sendPluginMessage("CR_DATA", data);
  }

  private DataOutputStream wrapForward(String server, String subchannel, byte[] data) {
    DataOutputStream out = new DataOutputStream();
    out.writeUTF("Forward");
    System.out.println("FORWARD >> server = " + server  + " >> channel = " + subchannel);
//    out.writeUTF(server);
//    out.writeUTF(subchannel);
    out.writeUTF("server1");
    out.writeUTF("death");

    out.writeShort(data.length);
    out.write(data);

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
