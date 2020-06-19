package com.civrealms.plugin.bukkit.respawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface BukkitRandomSpawn {
  Location getRandomSpawn(Player player);
}
