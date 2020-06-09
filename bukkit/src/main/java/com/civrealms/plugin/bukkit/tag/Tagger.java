package com.civrealms.plugin.bukkit.tag;

import org.bukkit.entity.Player;

public interface Tagger {
  void tag(Player victim, Player attacker);
  void untag(Player player);
}
