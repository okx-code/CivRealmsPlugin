package com.civrealms.plugin.bukkit.snitch;

import com.civrealms.plugin.bukkit.CivRealmsPlugin;
import com.civrealms.plugin.bukkit.stamina.StaminaDao;
import com.untamedears.JukeAlert.model.SuperSnitch;
import com.untamedears.JukeAlert.supersnitch.SuperSnitchHandler;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StaminaSuperSnitchHandler implements SuperSnitchHandler {
  private static final double MIN_STAMINA = 25;

  private static final double PLACE_STAMINA = 5; // Stamina cost to place super snitch
  private static final double REFUND_STAMINA = 3; // Stamina cost to refund super snitch
  private static final double REFUND_FUEL = 0.5; // Proportion of fuel refunded
  private static final double DAYS_PER_FUEL = 14;

  private final CivRealmsPlugin plugin;
  private final StaminaDao dao;

  public StaminaSuperSnitchHandler(CivRealmsPlugin plugin, StaminaDao dao) {
    this.plugin = plugin;
    this.dao = dao;
  }

  @Override
  public boolean placeSnitch(Player player, SuperSnitch superSnitch) {
    if (!dao.isEnabled()) {
      player.sendMessage(ChatColor.RED + "Database error");
      return false;
    }

    try {
      dao.writeLock();
      Optional<Double> optStamina = dao.getStamina(player.getUniqueId());
      if (!optStamina.isPresent()) {
        player.sendMessage(ChatColor.RED + "Stamina database error");
        return false;
      }
      double stamina = optStamina.get();

      double extraStamina = stamina - MIN_STAMINA;
      if (extraStamina < PLACE_STAMINA) {
        player.sendMessage(ChatColor.RED + "You need at least "
            + ChatColor.YELLOW + DecimalFormat.getInstance().format(PLACE_STAMINA)
            + ChatColor.RED + " stamina to place a super snitch.");
        return false;
      }

      stamina -= PLACE_STAMINA;
      dao.setStamina(player.getUniqueId(), stamina);
      player.sendMessage(ChatColor.YELLOW + "You have placed a super snitch for " + DecimalFormat.getInstance().format(PLACE_STAMINA) + " stamina. You now have " + DecimalFormat.getInstance().format(stamina) + " stamina.");
      superSnitch.refuel(1);

      return true;
    } finally {
      dao.unlock();
    }
  }

  @Override
  public void breakSnitch(Player player, SuperSnitch superSnitch) {
    if (!dao.isEnabled()) {
      player.sendMessage(ChatColor.RED + "Database error (unable to refund stamina");
    }

    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      try {
        dao.writeLock();
        Optional<Double> optStamina = dao.getStamina(player.getUniqueId());
        if (!optStamina.isPresent()) {
          player.sendMessage(ChatColor.RED + "Stamina database error (unable to refund stamina)");
          return;
        }

        double fuelStamina;
        Instant now = Instant.now();
        if (superSnitch.getFuel().isBefore(now)) {
          fuelStamina = 0;
        } else {
          fuelStamina = now.until(superSnitch.getFuel(), ChronoUnit.DAYS) / DAYS_PER_FUEL * REFUND_FUEL;
        }
        double refund = REFUND_STAMINA + Math.floor(fuelStamina);
        dao.setStamina(player.getUniqueId(), optStamina.get() + refund);
        player.sendMessage(ChatColor.YELLOW + "You have received " + DecimalFormat.getInstance().format(refund) + " stamina from breaking that super snitch.");
      } finally {
        dao.unlock();
      }
    });
  }

  @Override
  public boolean refuelSnitch(Player player, SuperSnitch superSnitch, int max) {
      try {
        dao.writeLock();
        Optional<Double> optStamina = dao.getStamina(player.getUniqueId());
        if (!optStamina.isPresent()) {
          player.sendMessage(ChatColor.RED + "Stamina database error");
          return false;
        }
        double stamina = optStamina.get();

        double extraStamina = stamina - MIN_STAMINA;

        int refuel = Math.min((int) extraStamina, Math.min(max, superSnitch.maxRefuel()));
        if (refuel == 0 || !superSnitch.refuel(refuel)) {
          player.sendMessage(ChatColor.YELLOW + "Super snitches may only be fueled for up to half a year at a time.");
          return false;
        }
        stamina -= refuel;
        player.sendMessage(ChatColor.YELLOW + "Refueled super snitch with " + refuel + " stamina. You now have " + DecimalFormat.getInstance().format(stamina) + " stamina.");
        dao.setStamina(player.getUniqueId(), stamina);

        return true;
      } finally {
        dao.unlock();
      }

  }
}
