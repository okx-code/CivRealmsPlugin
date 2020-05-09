package com.civrealms.plugin.bukkit.inventory;

import com.civrealms.plugin.common.packets.stream.DataInputStream;
import com.civrealms.plugin.common.packets.stream.DataOutputStream;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class InventorySerializer {
  public ItemStack[] deserialize(byte[] data) {
    DataInputStream in = new DataInputStream(data);

    int len = in.readInt();
    byte[] bytes = in.readByteArray();

    YamlConfiguration config = new YamlConfiguration();

    try {
      config.loadFromString(new String(bytes));
    } catch (InvalidConfigurationException e) {
      e.printStackTrace();
    }

    ItemStack[] deserialized = new ItemStack[len];

    Map<String, Object> values = config.getValues(false);
    for (Map.Entry<String, Object> value : values.entrySet()) {
      int i = Integer.parseInt(value.getKey());
      deserialized[i] = config.getSerializable(value.getKey(), ItemStack.class);
    }

    return deserialized;
  }

  public byte[] serialize(ItemStack[] items) {
    DataOutputStream out = new DataOutputStream();

    YamlConfiguration config = new YamlConfiguration();

    for (int i = 0; i < items.length; i++) {
      ItemStack item = items[i];
      if (item != null && item.getType() != Material.AIR) {
        config.set(String.valueOf(i), item);
      }
    }

    out.writeInt(items.length);
    out.writeByteArray(config.saveToString().getBytes());
    return out.toByteArray();
  }
}
