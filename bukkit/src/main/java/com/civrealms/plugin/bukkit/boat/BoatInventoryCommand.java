package com.civrealms.plugin.bukkit.boat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class BoatInventoryCommand implements CommandExecutor {

  private static final int PAGES = 3;

  private final BoatInventoryDao dao;

  public BoatInventoryCommand(BoatInventoryDao dao) {
    this.dao = dao;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }
    Player player = (Player) sender;
    if (!player.isInsideVehicle() || !(player.getVehicle() instanceof Boat)) {
      player.sendMessage(ChatColor.RED + "You must be in a boat to use this command!");
      return true;
    }

    int page = 1;
    if (args.length > 0) {
      try {
        page = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
        player.sendMessage(ChatColor.RED + "Invalid page number");
        return true;
      }
    }

    if (page < 1 || page > PAGES) {
      player.sendMessage(ChatColor.RED + "Page must be between 1 and " + PAGES + ".");
      return true;
    }

    int robotPage = page - 1;

    Boat boat = (Boat) player.getVehicle();
    BoatInventory boatInventory = dao.getBoatInventory(boat.getUniqueId());
    boolean populate = true;
    if (boatInventory == null) {
      populate = false;
      Location location = player.getLocation();
      boatInventory = new BoatInventory(
          player.getUniqueId(),
          location.getBlockX(),
          location.getBlockY(),
          location.getBlockZ(),
          new ItemStack[BoatInventory.PAGE_SIZE * PAGES]);
    }

    BoatInventoryHolder holder = new BoatInventoryHolder(boat.getUniqueId(), boatInventory, robotPage);
    Inventory inventory = Bukkit.createInventory(holder, BoatInventory.PAGE_SIZE, "Boat Inventory, page " + page + "/" + PAGES);
    holder.setInventory(inventory);

    if (populate) {
      ItemStack[] items = boatInventory.getPageItems(robotPage);
      inventory.setContents(items);
    }

    player.openInventory(inventory);

    return true;
  }
}
