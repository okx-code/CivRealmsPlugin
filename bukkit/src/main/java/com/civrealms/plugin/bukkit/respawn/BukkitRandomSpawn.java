package com.civrealms.plugin.bukkit.respawn;

import org.bukkit.Location;
import org.bukkit.World;

public interface BukkitRandomSpawn {
  Location getLocation(World w);
}
