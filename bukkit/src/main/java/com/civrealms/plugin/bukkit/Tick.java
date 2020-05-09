package com.civrealms.plugin.bukkit;

import org.bukkit.scheduler.BukkitRunnable;

public class Tick extends BukkitRunnable {
  private static int tick = 0;

  public static int get() {
    return tick;
  }

  @Override
  public void run() {
    ++tick;
  }
}
