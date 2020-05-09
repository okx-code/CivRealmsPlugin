package com.civrealms.plugin.common.packets.data;

import java.util.UUID;
import lombok.Data;

@Data
public class BoatData {
  private final UUID boat;
  private final boolean passenger;
  private final byte species;
}
