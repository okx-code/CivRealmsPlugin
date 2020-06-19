package com.civrealms.plugin.bukkit.boat;

import org.bukkit.entity.Boat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;

/**
 * Prevents players from opening GUIs while in boats, unless they are opening /binv
 */
public class InventoryOpenListener implements Listener {
  @EventHandler
  public void on(InventoryOpenEvent e) {
    if (e.getPlayer().getVehicle() instanceof Boat && !(e.getInventory().getHolder() instanceof BoatInventoryHolder)) {
      e.setCancelled(true);
    }
  }
}
