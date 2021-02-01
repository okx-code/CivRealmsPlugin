package com.civrealms.plugin.bukkit.stamina;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * A safe accessor of stamina data supporting asynchronous access using locking
 */
public class StaminaDao {
  private final DataSource source;
  private boolean enabled = false;

  public StaminaDao(DataSource source) {
    this.source = source;
    init();
  }

  private void init() {
    try (Connection connection = source.getConnection()) {
      enabled = connection.createStatement().executeQuery("SHOW TABLES LIKE 'player_data'").next();
      if (!enabled) {
        System.out.println("Cannot find 'player_data' table");
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      enabled = false;
    }
  }

  public boolean isEnabled() {
    return enabled;
  }

  /**
   * Locks the stamina table with a read/write lock, for exclusive access.
   * @return true if the lock has been obtained
   */
  public boolean writeLock() {
    if (!enabled) return false;

    try (Connection connection = source.getConnection()) {
      connection.createStatement().executeUpdate("LOCK TABLES player_data WRITE");
      return true;
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  /**
   * Unlocks any locks currently held by this session
   * @return true if no error was raised
   */
  public boolean unlock() {
    if (!enabled) return false;

    try (Connection connection = source.getConnection()) {
      connection.createStatement().executeUpdate("UNLOCK TABLES");
      return true;
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public Optional<Double> getStamina(UUID uuid) {
    if (!enabled) return Optional.empty();

    Objects.requireNonNull(uuid, "uuid");
    try (Connection connection = source.getConnection()) {
      PreparedStatement statement = connection.prepareStatement("SELECT stamina FROM player_data WHERE player_uuid = ?");
      statement.setString(1, uuid.toString());

      ResultSet results = statement.executeQuery();
      if (results.next()) {
        return Optional.of(results.getDouble("stamina"));
      } else {
        return Optional.empty();
      }
    } catch (SQLException ex) {
      ex.printStackTrace();
      return Optional.empty();
    }
  }

  public boolean setStamina(UUID uuid, double stamina) {
    if (!enabled) return false;

    Objects.requireNonNull(uuid, "uuid");
    try (Connection connection = source.getConnection()) {
      PreparedStatement statement = connection.prepareStatement("UPDATE player_data SET stamina = ? WHERE player_uuid = ?");
      statement.setDouble(1, stamina);
      statement.setString(2, uuid.toString());
      statement.executeUpdate();
      return true;
    } catch (SQLException ex) {
      ex.printStackTrace();
      return false;
    }
  }

  public boolean isStamina(ItemStack item) {
    return item != null
        && item.getType() == Material.GOLDEN_APPLE
        && item.getDurability() == 1
        && item.hasItemMeta() && item.getItemMeta().hasLore()
        && item.getItemMeta().getLore().get(0).contentEquals("Prolonged physical");
  }
}
