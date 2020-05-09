package com.civrealms.plugin.common.shard;

public interface Shard {
  String getServer();
  boolean contains(int x, int y, int z);
  ShardType getType();

  /**
   * Copy the shard with an increased radius of at least a chunk (16 blocks),
   * so when you leave a server the shards don't overlap and you get sent back and forth.
   * This is called when a shard is sent to the same server as it represents
   */
  Shard increaseRadius();
}
