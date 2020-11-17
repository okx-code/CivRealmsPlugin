package com.civrealms.plugin.bukkit.respawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface BukkitRandomSpawn {
  Location getRandomSpawn(World world, Player player);
}
