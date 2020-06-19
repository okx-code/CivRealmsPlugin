package com.civrealms.plugin.bukkit.inventory.log;

import com.civrealms.plugin.common.Location;
import java.time.Instant;
import java.util.UUID;
import org.bukkit.inventory.ItemStack;

public class InventoryLog {
  private final UUID player;
  private final Instant timestamp;
  private final String server;
  private final Location location;
  private final ItemStack[] inventory;
  private final String metadata;

  public InventoryLog(UUID player, Instant timestamp, String server, Location location,
      ItemStack[] inventory, String metadata) {
    this.player = player;
    this.timestamp = timestamp;
    this.server = server;
    this.location = location;
    this.inventory = inventory;
    this.metadata = metadata;
  }

  public UUID getPlayer() {
    return player;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public String getServer() {
    return server;
  }

  public Location getLocation() {
    return location;
  }

  public ItemStack[] getInventory() {
    return inventory;
  }

  public String getMetadata() {
    return metadata;
  }
}
