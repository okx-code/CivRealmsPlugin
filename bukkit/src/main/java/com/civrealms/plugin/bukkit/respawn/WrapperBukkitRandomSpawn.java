package com.civrealms.plugin.bukkit.respawn;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WrapperBukkitRandomSpawn implements BukkitRandomSpawn {
  private final RandomSpawn spawn;

  public WrapperBukkitRandomSpawn(RandomSpawn spawn) {
    this.spawn = spawn;
  }

  @Override
  public Location getRandomSpawn(Player player) {
    com.civrealms.plugin.common.Location location = spawn.getLocation();
    Location teleport = new Location(player.getWorld(), location.getX() + 0.5, 0, location.getZ() + 0.5);
    teleport.setY(player.getWorld().getHighestBlockYAt(teleport));
    return teleport;
  }
}
