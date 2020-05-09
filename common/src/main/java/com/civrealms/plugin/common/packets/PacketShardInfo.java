package com.civrealms.plugin.common.packets;

import com.civrealms.plugin.common.packet.Packet;
import com.civrealms.plugin.common.packets.stream.DataInputStream;
import com.civrealms.plugin.common.packets.stream.DataOutputStream;
import com.civrealms.plugin.common.shard.CircleShard;
import com.civrealms.plugin.common.shard.AquaNether;
import com.civrealms.plugin.common.shard.Shard;
import com.civrealms.plugin.common.shard.ShardType;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class PacketShardInfo implements Packet {
  private String transitiveShard;
  private AquaNether aquaNether;
  private Set<Shard> shards;

  @Override
  public void read(DataInputStream in) {
    transitiveShard = in.readUTF();

    aquaNether = readAquaNether(in);

    int len = in.readInt();
    shards = new HashSet<>(len);
    for (int i = 0; i < len; i++) {
      Shard shard = readShard(in);
      if (shard != null) {
        shards.add(shard);
      }
    }
  }

  @Override
  public void write(DataOutputStream out) {
    out.writeUTF(transitiveShard);

    writeAquaNether(out, aquaNether);

    out.writeInt(shards.size());
    for (Shard shard : shards) {
      writeShard(out, shard);
    }
  }

  private Shard readShard(DataInputStream in) {
    int index = in.readInt();
    if (index >= 0 && index < ShardType.values().length) {
      /*boolean inverted = in.readBoolean();*/
      ShardType type = ShardType.values()[index];

      Shard shard = null;

      if (type == ShardType.CIRCLE) {
        String server = in.readUTF();
        int x = in.readInt();
        int z = in.readInt();
        double radius = in.readDouble();

        shard = new CircleShard(server, x, z, radius);
      }

      if (shard != null) {
        /*if (inverted) {
          shard = new InvertedShard(shard);
        }*/
        return shard;
      }
    }
    return null;
  }

  private AquaNether readAquaNether(DataInputStream in) {
    boolean exists = in.readBoolean();
    if (!exists) {
      return null;
    }

    boolean isTop = in.readBoolean();
    float yTeleport = in.readFloat();
    float ySpawn = in.readFloat();
    boolean hasOpposite = in.readBoolean();
    String oppositeServer = null;
    if (hasOpposite) {
      oppositeServer = in.readUTF();
    }

    return new AquaNether(isTop, yTeleport, ySpawn, oppositeServer);
  }

  private void writeShard(DataOutputStream out, Shard shard) {
    ShardType type = shard.getType();
    out.writeInt(type.ordinal());

    /*out.writeBoolean(shard instanceof InvertedShard);*/

    if (type == ShardType.CIRCLE) {
      CircleShard circleShard = (CircleShard) shard;
      out.writeUTF(circleShard.getServer());
      out.writeInt(circleShard.getCenterX());
      out.writeInt(circleShard.getCenterZ());
      out.writeDouble(circleShard.getRadius());
    }
  }

  private void writeAquaNether(DataOutputStream out, AquaNether aquaNether) {
    if (aquaNether == null) {
      out.writeBoolean(false);
    } else {
      out.writeBoolean(true);

      out.writeBoolean(aquaNether.isTop());
      out.writeFloat(aquaNether.getYTeleport());
      out.writeFloat(aquaNether.getYSpawn());

      String oppositeServer = aquaNether.getOppositeServer();
      if (oppositeServer == null) {
        out.writeBoolean(false);
      } else {
        out.writeBoolean(true);
        out.writeUTF(oppositeServer);
      }
    }
  }
}
