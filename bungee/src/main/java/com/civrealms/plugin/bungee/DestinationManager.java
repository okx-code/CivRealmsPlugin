package com.civrealms.plugin.bungee;

import java.util.HashMap;
import java.util.Map;
import net.md_5.bungee.api.config.ServerInfo;

public class DestinationManager {
  private final Map<String, ServerInfo> identified = new HashMap<>();

  public void setServer(String name, ServerInfo server) {
    System.out.println("IDENTIFIED >> " + name + " >> " + server);
    identified.put(name, server);
  }

  public ServerInfo getServer(String name) {
    System.out.println("WHOIS >> " + name + " >> " + identified.get(name));
    return identified.get(name);
  }
}
