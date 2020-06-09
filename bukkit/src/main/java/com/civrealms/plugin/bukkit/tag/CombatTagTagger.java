//package com.civrealms.plugin.bukkit.tag;
//
//import net.minelink.ctplus.CombatTagPlus;
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//
//public class CombatTagTagger implements Tagger {
//  private CombatTagPlus tagPlugin;
//
//  public CombatTagTagger() {
//    tagPlugin = (CombatTagPlus) Bukkit.getServer().getPluginManager().getPlugin("CombatTagPlus");
//  }
//
//  @Override
//  public void tag(Player victim, Player attacker) {
//    tagPlugin.getTagManager().tag(victim, attacker);
//  }
//
//  @Override
//  public void untag(Player player) {
//    tagPlugin.getTagManager().untag(player.getUniqueId());
//  }
//}
