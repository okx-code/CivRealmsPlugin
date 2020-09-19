package com.civrealms.plugin.common;

import lombok.Data;

@Data
public class Location {
  private final int x;
  private final int y;
  private final int z;

  @Override
  public String toString() {
    return "(" + x + ", " + y + ", " + z + ")";
  }
}
