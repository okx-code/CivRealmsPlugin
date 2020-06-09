package com.civrealms.plugin.bukkit;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class testlistener implements Listener {
  @EventHandler
  public void on(AsyncPlayerPreLoginEvent e) {
    try {
      System.out.println("LOGIN START " + e.getName());
//      Thread.sleep(2000);
      System.out.println("LOGIN END " + e.getName());
    } catch (Exception interruptedException) {
      interruptedException.printStackTrace();
    }
  }
}
