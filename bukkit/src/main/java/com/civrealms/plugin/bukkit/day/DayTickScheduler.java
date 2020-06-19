package com.civrealms.plugin.bukkit.day;

import org.bukkit.scheduler.BukkitRunnable;

public class DayTickScheduler extends BukkitRunnable {
  private final long dayLengthTicks;

  public DayTickScheduler(long dayLengthTicks) {
    this.dayLengthTicks = dayLengthTicks;
  }

  @Override
  public void run() {

  }
}
