package com.civrealms.plugin.bukkit.boat;

import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class BoatInventory {
  public static final int PAGE_SIZE = 54;

  private UUID lastPlayer;
  private int x;
  private int y;
  private int z;

  private final ItemStack[] items;

  public BoatInventory(UUID lastPlayer, int x, int y, int z, ItemStack[] items) {
    this.lastPlayer = lastPlayer;
    this.x = x;
    this.y = y;
    this.z = z;
    this.items = items;
  }

  public ItemStack[] getItems() {
    return items;
  }

  public void setLastPlayer(UUID lastPlayer) {
    this.lastPlayer = lastPlayer;
  }

  public void setLocation(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public UUID getLastPlayer() {
    return lastPlayer;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getZ() {
    return z;
  }

  public ItemStack[] getPageItems(int page) {
    ItemStack[] pageItems = new ItemStack[PAGE_SIZE];
    for (int i = 0; i < PAGE_SIZE; i++) {
      ItemStack item = items[i + (page * PAGE_SIZE)];
      pageItems[i] = item == null ? null : item.clone();
    }
    return pageItems;
  }

  public void setPageItems(int page, ItemStack[] pageItems) {
    for (int i = 0; i < PAGE_SIZE; i++) {
      ItemStack item = pageItems[i];
      items[i + (page * PAGE_SIZE)] = item == null ? null : item.clone();
    }
  }
}
