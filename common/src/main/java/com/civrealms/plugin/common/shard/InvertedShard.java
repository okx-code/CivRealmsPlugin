package com.civrealms.plugin.common.shard;

public class InvertedShard implements Shard {
  private final Shard shard;

  public InvertedShard(Shard shard) {
    this.shard = shard;
  }

  @Override
  public String getServer() {
    return shard.getServer();
  }

  @Override
  public boolean contains(int x, int y, int z) {
    return !shard.contains(x, y, z);
  }

  @Override
  public ShardType getType() {
    return shard.getType();
  }

  @Override
  public Shard increaseRadius() {
    return new InvertedShard(shard.increaseRadius());
  }
}
