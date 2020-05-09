package com.civrealms.plugin.bukkit.inventory;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.bukkit.inventory.ItemStack;

public class GZIPInventorySerializer extends InventorySerializer {

  @Override
  public byte[] serialize(ItemStack[] items) {
    byte[] serialize = super.serialize(items);

    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      GZIPOutputStream gzip = new GZIPOutputStream(out);
      gzip.write(serialize);
      gzip.close();

      return out.toByteArray();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Override
  public ItemStack[] deserialize(byte[] data) {
    try {
      GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));

      byte[] out = ByteStreams.toByteArray(gzip);
      gzip.close();
      return super.deserialize(out);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
