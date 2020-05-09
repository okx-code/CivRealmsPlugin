package com.civrealms.plugin.bukkit.boat;

import java.util.UUID;

public interface BoatInventoryDao {
  BoatInventory getBoatInventory(UUID uuid);
  void saveBoatInventory(UUID uuid, BoatInventory inventory);
  void changeId(UUID from, UUID to);
  void deleteBoatInventory(UUID uuid);
}
