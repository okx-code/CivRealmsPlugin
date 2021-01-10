package com.civrealms.plugin.bukkit.boat;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Boat;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class BoatInventoryListener implements Listener {

  private final BoatInventoryDao dao;

  public BoatInventoryListener(BoatInventoryDao dao) {
    this.dao = dao;
  }

  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onBoatDestroy(VehicleDestroyEvent e) {
    if (!(e.getVehicle() instanceof Boat)) {
      return;
    }
    spillBoat((Boat) e.getVehicle());
  }

//  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBoatMove(VehicleMoveEvent e) {
    if (!(e.getVehicle() instanceof Boat)) {
      return;
    }
    // TODO BETTER DETECTION
    // this detection actually sucks
    // i just copied it from the old plugin
    Material boatOn = e.getTo().getBlock().getType();
    if (boatOn != Material.STATIONARY_WATER && boatOn != Material.WATER && boatOn != Material.AIR) {
      Boat boat = (Boat) e.getVehicle();
      spillBoat(boat);
      boat.remove();
    }
  }

  private void spillBoat(Boat boat) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      InventoryView view = player.getOpenInventory();
      Inventory top = view.getTopInventory();
      if (top != null && top.getHolder() instanceof BoatInventoryHolder) {
        BoatInventoryHolder holder = (BoatInventoryHolder) top.getHolder();
        if (holder.getBoat().equals(boat.getUniqueId())) {
          player.closeInventory();
        }
      }
    }

    BoatInventory inventory = dao.getBoatInventory(boat.getUniqueId());
    if (inventory == null) {
      return;
    }
    Location loc = boat.getLocation();
    for (ItemStack item : inventory.getItems()) {
      if (item != null) {
        boat.getWorld().dropItemNaturally(loc, item);
      }
    }

    // close player inventories
    // spill items
    dao.deleteBoatInventory(boat.getUniqueId());
    boat.remove();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onInventoryClose(InventoryCloseEvent event) {
    Inventory inventory = event.getInventory();
    HumanEntity player = event.getPlayer();
    if (!(inventory.getHolder() instanceof BoatInventoryHolder) /*|| !(player.getVehicle() instanceof Boat)*/) {
      return;
    }
    BoatInventoryHolder holder = (BoatInventoryHolder) inventory.getHolder();
    int page = holder.getPage();

    Location location = player.getLocation();

    BoatInventory boatInventory = holder.getBoatInventory();
    boatInventory.setLocation(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    boatInventory.setLastPlayer(player.getUniqueId());
    boatInventory.setPageItems(page, inventory.getContents());

    dao.saveBoatInventory(holder.getBoat(), boatInventory);
  }
}
