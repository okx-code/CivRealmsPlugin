package com.civrealms.plugin.bukkit.boat;

import java.util.UUID;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BoatInventoryHolder implements InventoryHolder {
  private final UUID boat;
  private final BoatInventory boatInventory;
  private final int page;
  private Inventory inventory;

  public BoatInventoryHolder(UUID boat, BoatInventory boatInventory, int page) {
    this.boat = boat;
    this.boatInventory = boatInventory;
    this.page = page;
  }

  public UUID getBoat() {
    return boat;
  }

  @Override
  public Inventory getInventory() {
    return inventory;
  }

  public void setInventory(Inventory inventory) {
    this.inventory = inventory;
  }

  public int getPage() {
    return page;
  }

  public BoatInventory getBoatInventory() {
    return boatInventory;
  }
}
