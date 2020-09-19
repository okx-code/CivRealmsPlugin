package com.civrealms.plugin.bukkit.inventory.log;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InventoryRestoreCommand implements CommandExecutor {
  private final InventoryLogDao dao;

  public InventoryRestoreCommand(InventoryLogDao dao) {
    this.dao = dao;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length < 1 || !(sender instanceof Player)) {
      return false;
    }
    int id;
    try {
      id = Integer.parseInt(args[0]);
    } catch (IllegalArgumentException e) {
      return false;
    }

    InventoryLog inventoryLog = dao.loadLogInventory(id);
    Player player = (Player) sender;
    if (inventoryLog == null) {
      player.sendMessage("Cannot load inventory for that ID");
      return true;
    }

    UUID playerId = inventoryLog.getPlayer();
    String name = Bukkit.getOfflinePlayer(playerId).getName();

    player.sendMessage("Metadata: " + inventoryLog.getMetadata()
    + "\nServer: " + inventoryLog.getServer()
    + "\nLocation: " + inventoryLog.getLocation()
    + "\nPlayer: " + (name == null ? "Unknown" : name) + " (" + playerId + ")"
    + "\nTimestamp: " + inventoryLog.getTimestamp());

    player.getInventory().setContents(inventoryLog.getInventory());

    return true;
  }
}
