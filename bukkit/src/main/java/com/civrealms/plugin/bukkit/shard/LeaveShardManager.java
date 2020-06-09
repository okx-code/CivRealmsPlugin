package com.civrealms.plugin.bukkit.shard;

import com.civrealms.plugin.bukkit.Tick;
import java.util.Map;
import java.util.WeakHashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class LeaveShardManager implements Listener {
  private static final long TIMEOUT = 100;

  private final Map<Player, Integer> leaving = new WeakHashMap<>();

  public boolean isLeaving(Player player) {
    if (!leaving.containsKey(player)) {
      return false;
    }

    int left = leaving.get(player);
    int duration = Tick.get() - left;

    if (duration < TIMEOUT) {
      return true;
    } else {
      leaving.remove(player);
      return false;
    }
  }

  public void setLeaving(Player player) {
    leaving.put(player, Tick.get());
  }

  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent e) {
    leaving.remove(e.getPlayer());
  }

}
