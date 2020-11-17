package com.civrealms.plugin.bukkit.respawn;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;

public class CrimeoRandomSpawn implements BukkitRandomSpawn {
  private final int spawnWidth;
  private final int spawnHeight;

  private final int centreX;
  private final int centreZ;

  public CrimeoRandomSpawn(int spawnWidth, int spawnHeight, int centreX, int centreZ) {
    this.spawnWidth = spawnWidth;
    this.spawnHeight = spawnHeight;
    this.centreX = centreX;
    this.centreZ = centreZ;
  }

  @Override
  public Location getLocation(World w) {
    int xspwn = (int) ((Math.random() * spawnWidth)
        + centreX - (spawnWidth / 2));
    int zspwn = (int) ((Math.random() * spawnHeight)
        + centreZ - (spawnHeight / 2));

    Block highestBlock = getHighestCustomForRespawn(xspwn, zspwn, w);
    while (highestBlock == null){
      xspwn = (int) ((Math.random() * spawnWidth)
          + centreX - (spawnWidth / 2));
      zspwn = (int) ((Math.random() * spawnHeight)
          + centreZ - (spawnHeight / 2));
      highestBlock = getHighestCustomForRespawn(xspwn, zspwn, w);
    }
    int highestBlockY = highestBlock.getY();
    return new Location(w, xspwn+0.5, highestBlockY+1, zspwn+0.5);
  }

  public static Block getHighestCustomForRespawn(int x, int z, World w){
    for (int y = 255; y > -1; y--){
      Block b = w.getBlockAt(x,y,z);
      Material type = b.getType();
      if (type == Material.AIR){
        continue;
      }
      if(type == Material.WATER || type == Material.STATIONARY_WATER //things that might kill you
          || type == Material.LAVA || type == Material.STATIONARY_LAVA
          || type == Material.BEDROCK || type == Material.OBSIDIAN
          || type == Material.CACTUS || type == Material.MAGMA){
        return null;
      }
      if (!type.isSolid() || type == Material.SIGN || type == Material.WALL_SIGN || type == Material.SIGN_POST || type == Material.TRAP_DOOR //things that might make you drop a long way but if skipped also won't suffocate you
          || type == Material.CARPET || type == Material.THIN_GLASS || type == Material.STAINED_GLASS_PANE || type == Material.IRON_FENCE
          || type == Material.LADDER || type == Material.END_ROD || type == Material.WOOD_PLATE || type == Material.IRON_PLATE
          || type == Material.GOLD_PLATE || type == Material.IRON_DOOR || type == Material.WOODEN_DOOR || type == Material.BANNER
          || type == Material.WALL_BANNER || type == Material.FENCE || type == Material.FENCE_GATE || type == Material.BREWING_STAND
          || type == Material.NETHER_FENCE || type == Material.DAYLIGHT_DETECTOR){
        continue;
      }
      Biome biome = b.getBiome();
      if (biome.equals(Biome.TAIGA_COLD)
          || biome.equals(Biome.REDWOOD_TAIGA)
          || biome.equals(Biome.PLAINS)
          || biome.equals(Biome.ICE_FLATS)
          || biome.equals(Biome.ICE_MOUNTAINS)
          || biome.equals(Biome.FROZEN_OCEAN)
          || biome.equals(Biome.FROZEN_RIVER)
          || biome.equals(Biome.OCEAN)
          || biome.equals(Biome.MUTATED_PLAINS)
          || biome.equals(Biome.EXTREME_HILLS)
          || biome.equals(Biome.MUTATED_DESERT)
          || biome.equals(Biome.MUSHROOM_ISLAND_SHORE)
          || biome.equals(Biome.MUSHROOM_ISLAND)){
        return null;
      }
      return b;
    }
    return null;
  }
}
