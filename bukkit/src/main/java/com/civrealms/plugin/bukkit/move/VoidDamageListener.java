package com.civrealms.plugin.bukkit.move;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Cancels all damage in the void
 * Honestly this shouldn't be needed unless the server is lagging really badly but it stops people losing all their stuff
 */
public class VoidDamageListener implements Listener {

  @EventHandler
  public void on(EntityDamageEvent e) {
    if (e.getEntityType() == EntityType.PLAYER && e.getCause() == DamageCause.VOID) {
      e.setCancelled(true);
    }
  }
}
