package com.civrealms.plugin.bukkit.day;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

public class DayTickScheduler extends BukkitRunnable {
  private long dayProgress;
  private final long dayLengthTicks;

  public DayTickScheduler(long dayLengthTicks) {
    this.dayLengthTicks = dayLengthTicks;
  }

  @Override
  public void run() {
    dayProgress += 10;
    double div = dayProgress / (double) dayLengthTicks;
    if (div > 1) {
      div = 1;
      dayProgress = 0;
    }
    long ticks = Math.round(24000 * div);

    for (World world : Bukkit.getWorlds()) {
      long baseTicks = world.getFullTime() - (world.getFullTime() % 24000L);
      world.setFullTime(baseTicks + ticks);
    }
  }
}
