package com.civrealms.plugin.bukkit.inventory.log;

import java.util.List;
import java.util.UUID;

/**
 * Logs the inventory of a player at various critical stages.
 * For example, the inventory is logged when a player:
 * - changes shards
 * - enters or leaves the Aqua Nether
 * - dies
 */
public interface InventoryLogDao {

  /**
   * Saves the specified log to the database
   * @param log the inventory log to save to the database
   * @return the auto-incremented ID of the saved row in the database or -1 if saving was unsuccessful.
   */
  int saveInventoryLog(InventoryLog log);

  List<InventoryLog> getRecentInventoryLogs(UUID player, int limit, int offset);

  InventoryLog loadLogInventory(int id);
}
