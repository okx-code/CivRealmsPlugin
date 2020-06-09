
package com.civrealms.plugin.bukkit.shard;

public class ShardIdentifyScheduler implements Runnable {
  private final ShardManager shardManager;

  public ShardIdentifyScheduler(ShardManager shardManager) {
    this.shardManager = shardManager;
  }

  @Override
  public void run() {
    shardManager.sendIdentify(false);
  }
}
