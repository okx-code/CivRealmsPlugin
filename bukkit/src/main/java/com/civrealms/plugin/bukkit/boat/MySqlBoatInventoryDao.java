package com.civrealms.plugin.bukkit.boat;

import com.civrealms.plugin.bukkit.inventory.GZIPInventorySerializer;
import com.civrealms.plugin.bukkit.inventory.InventorySerializer;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import javax.sql.DataSource;
import org.bukkit.inventory.ItemStack;

public class MySqlBoatInventoryDao implements BoatInventoryDao {
  private static final InventorySerializer serializer = new GZIPInventorySerializer();

  private static final String CREATE_INVENTORY = "CREATE TABLE IF NOT EXISTS boatinv ("
      + "uuid VARCHAR(36),"
      + "inventory BLOB,"
      + "lastPlayer VARCHAR(36),"
      + "x INT,"
      + "y INT,"
      + "z INT,"
      + "PRIMARY KEY (uuid))";
  private static final String GET_INVENTORY = "SELECT * FROM boatinv WHERE uuid = ?";
  private static final String SAVE_INVENTORY = "REPLACE INTO boatinv (uuid, inventory, lastPlayer, x, y, z) VALUES (?, ?, ?, ?, ?, ?)";
  private static final String CHANGE_UUID = "UPDATE boatinv SET uuid = ? WHERE uuid = ?";
  private static final String DELETE_INVENTORY = "DELETE FROM boatinv WHERE uuid = ?";

  private final DataSource source;

  public MySqlBoatInventoryDao(String host, int port, String database, String username, String password) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
    config.setUsername(username);
    config.setPassword(password);

    source = new HikariDataSource(config);
    init();
  }

  private void init() {
    try (Connection conn = source.getConnection()) {
      conn.createStatement().executeUpdate(CREATE_INVENTORY);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public BoatInventory getBoatInventory(UUID uuid) {
    try (Connection conn = source.getConnection()) {
      PreparedStatement statement = conn.prepareStatement(GET_INVENTORY);
      statement.setString(1, uuid.toString());

      ResultSet resultSet = statement.executeQuery();
      if (resultSet.next()) {
        UUID lastPlayer = UUID.fromString(resultSet.getString("lastPlayer"));
        byte[] inventoryData = resultSet.getBytes("inventory");
        int x = resultSet.getInt("x");
        int y = resultSet.getInt("y");
        int z = resultSet.getInt("z");

        ItemStack[] inventory = serializer.deserialize(inventoryData);

        return new BoatInventory(lastPlayer, x, y, z, inventory);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public void saveBoatInventory(UUID uuid, BoatInventory inventory) {
    try (Connection conn = source.getConnection()) {
      PreparedStatement statement = conn.prepareStatement(SAVE_INVENTORY);
      statement.setString(1, uuid.toString());
      statement.setBytes(2, serializer.serialize(inventory.getItems()));
      statement.setString(3, inventory.getLastPlayer().toString());
      statement.setInt(4, inventory.getX());
      statement.setInt(5, inventory.getY());
      statement.setInt(6, inventory.getZ());

      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void changeId(UUID from, UUID to) {
    try (Connection conn = source.getConnection()) {
      PreparedStatement statement = conn.prepareStatement(CHANGE_UUID);
      statement.setString(1, to.toString());
      statement.setString(2, from.toString());

      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void deleteBoatInventory(UUID uuid) {
    try (Connection conn = source.getConnection()) {
      PreparedStatement statement = conn.prepareStatement(DELETE_INVENTORY);
      statement.setString(1, uuid.toString());

      statement.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
