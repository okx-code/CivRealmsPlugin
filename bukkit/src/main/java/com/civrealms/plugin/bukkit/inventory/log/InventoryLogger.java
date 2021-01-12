package com.civrealms.plugin.bukkit.inventory.log;

import com.civrealms.plugin.common.Location;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;
import net.minelink.ctplus.CombatTagPlus;
import net.minelink.ctplus.compat.api.NpcPlayerHelper;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class InventoryLogger {
  private final Plugin plugin;
  private final String server;
  private final Logger logger;
  private final InventoryLogDao dao;

  public InventoryLogger(Plugin plugin, String server, Logger logger,
      InventoryLogDao dao) {
    this.plugin = plugin;
    this.server = server;
    this.logger = logger;
    this.dao = dao;
  }

  public void log(Player player, ItemStack[] inventory, String metadata) {
    log(player, inventory, metadata, null);
  }

  public void log(Player player, ItemStack[] inventory, String metadata, Consumer<Integer> callback) {
    UUID uuid;
    if (Bukkit.getPluginManager().isPluginEnabled("CombatTagPlus")) {
      NpcPlayerHelper npcPlayerHelper = JavaPlugin.getPlugin(CombatTagPlus.class)
          .getNpcPlayerHelper();
      if (npcPlayerHelper.isNpc(player)) {
        uuid = npcPlayerHelper.getIdentity(player).getId();
      } else {
        uuid = player.getUniqueId();
      }
    } else {
      uuid = player.getUniqueId();
    }
    metadata = "CT_" + metadata;
    if (isInventoryEmpty(inventory)) {
      logger.info(player.getName() + " (" + metadata + ") snapshot not saved due to empty inventory.");
      return;
    }

    org.bukkit.Location location = player.getLocation();
    InventoryLog log = new InventoryLog(
        uuid,
        Instant.now(),
        server,
        new Location(location.getBlockX(), location.getBlockY(), location.getBlockZ()),
        inventory,
        metadata
    );
    String metadataFinal = metadata;
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      int id = dao.saveInventoryLog(log);
      logger.info(player.getName() + " saved inventory snapshot #" + id + " (" + metadataFinal + ")");
      if (callback != null) {
        callback.accept(id);
      }
    });
  }

  private boolean isInventoryEmpty(ItemStack[] items) {
    for (ItemStack item : items) {
      if (item != null) {
        return false;
      }
    }
    return true;
  }
}
