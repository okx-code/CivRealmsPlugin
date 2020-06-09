package com.civrealms.plugin.bukkit.respawn;

import com.civrealms.plugin.common.Location;

public interface RandomSpawn {

  /**
   * Generates a random spawn location for a player
   * The Y coordinate in the returned location is ignored.
   * @return the location for a player to random spawn at
   */
  Location getLocation();
}
