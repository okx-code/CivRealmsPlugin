package com.civrealms.plugin.bukkit.respawn;

import com.civrealms.plugin.common.Location;

public class DummyRandomSpawn implements RandomSpawn {

  @Override
  public Location getLocation() {
    return new Location(-3601, 0, 600);
  }
}
