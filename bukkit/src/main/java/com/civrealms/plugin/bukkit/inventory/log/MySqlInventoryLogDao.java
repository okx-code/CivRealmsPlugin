package com.civrealms.plugin.bukkit.inventory.log;

import com.civrealms.plugin.bukkit.inventory.GZIPInventorySerializer;
import com.civrealms.plugin.common.Location;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.bukkit.inventory.ItemStack;

public class MySqlInventoryLogDao implements InventoryLogDao {

  private static final String CREATE_INVENTORY_LOG = "CREATE TABLE IF NOT EXISTS inventory_log ("
      + "id INT NOT NULL AUTO_INCREMENT,"
      + "created TIMESTAMP,"
      + "uuid VARCHAR(36),"
      + "server TEXT,"
      + "x INT,"
      + "y INT,"
      + "z INT,"
      + "metadata TEXT,"
      + "inventory BLOB,"
      + "PRIMARY KEY (id))";
  private static final String SAVE_INVENTORY_LOG = "INSERT INTO inventory_log (created, uuid, server, x, y, z, metadata, inventory) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
  private static final String LOAD_INVENTORY = "SELECT * FROM inventory_log WHERE id = ?";

  private final DataSource source;

  public MySqlInventoryLogDao(String host, int port, String database, String username, String password) {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
    config.setUsername(username);
    config.setPassword(password);

    source = new HikariDataSource(config);
    init();
  }

  private void init() {
    try (Connection conn = source.getConnection()) {
      conn.createStatement().executeUpdate(CREATE_INVENTORY_LOG);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public int saveInventoryLog(InventoryLog log) {
    try (Connection conn = source.getConnection()) {
      PreparedStatement statement = conn
          .prepareStatement(SAVE_INVENTORY_LOG, Statement.RETURN_GENERATED_KEYS);
      statement.setTimestamp(1, Timestamp.from(log.getTimestamp()));
      statement.setString(2, log.getPlayer().toString());
      statement.setString(3, log.getServer());
      statement.setInt(4, log.getLocation().getX());
      statement.setInt(5, log.getLocation().getY());
      statement.setInt(6, log.getLocation().getZ());
      statement.setString(7, log.getMetadata());
      statement.setBytes(8, new GZIPInventorySerializer().serialize(log.getInventory()));

      int affectedRows = statement.executeUpdate();
      if (affectedRows == 0) {
        return -1;
      }

      try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
        if (generatedKeys.next()) {
          return generatedKeys.getInt(1);
        } else {
          return -1;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return -1;
    }
  }

  @Override
  public List<InventoryLog> getRecentInventoryLogs(UUID player, int limit, int offset) {
    throw new UnsupportedOperationException();
  }

  @Override
  public InventoryLog loadLogInventory(int id) {
    try (Connection conn = source.getConnection()) {
      PreparedStatement statement = conn.prepareStatement(LOAD_INVENTORY);
      statement.setInt(1, id);

      ResultSet results = statement.executeQuery();
      if (results.next()) {
        Instant created = results.getTimestamp("created").toInstant();
        UUID uuid = UUID.fromString(results.getString("uuid"));
        String server = results.getString("server");
        int x = results.getInt("x");
        int y = results.getInt("y");
        int z = results.getInt("z");
        String metadata = results.getString("metadata");
        ItemStack[] inventory = new GZIPInventorySerializer().deserialize(results.getBytes("inventory"));

        return new InventoryLog(uuid, created, server, new Location(x, y, z), inventory, metadata);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }
}
