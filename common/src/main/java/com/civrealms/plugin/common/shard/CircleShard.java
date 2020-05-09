package com.civrealms.plugin.common.shard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class CircleShard implements Shard {
  private final String server;
  private final int centerX;
  private final int centerZ;
  private final double radius;

  @Override
  public boolean contains(int x, int y, int z) {
    int xDist = Math.abs(centerX - x);
    int zDist = Math.abs(centerZ - z);

    double distanceSquared = xDist * xDist + zDist * zDist;

    return distanceSquared <= radius * radius;
  }

  @Override
  public ShardType getType() {
    return ShardType.CIRCLE;
  }

  @Override
  public Shard increaseRadius() {
    return new CircleShard(server, centerX, centerZ, radius + 24);
  }
}
