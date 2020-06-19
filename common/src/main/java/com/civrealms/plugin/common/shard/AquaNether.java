package com.civrealms.plugin.common.shard;

import lombok.Data;

@Data
public class AquaNether {
  private final boolean isTop;
  private final float yTeleport;
  private final float ySpawn;
  private final String oppositeServer;
  private final int oceanHeight;
}
