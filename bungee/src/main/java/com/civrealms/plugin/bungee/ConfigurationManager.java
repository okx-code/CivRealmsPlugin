package com.civrealms.plugin.bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigurationManager {
  private final File dataFolder;
  private final ConfigurationProvider provider;

  public ConfigurationManager(File dataFolder) {
    this.dataFolder = dataFolder;
    provider = ConfigurationProvider.getProvider(YamlConfiguration.class);
  }

  public Configuration get(String name) {
    File file = getFile(name);
    if (!file.exists()) {
      copy(name);
    }

    return load(name);
  }

  public void save(Configuration configuration, String name) {
    try {
      provider.save(configuration, getFile(name));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private Configuration load(String name) {
    try {
      return provider.load(getFile(name));
    } catch (IOException e) {
      return null;
    }
  }

  private File getFile(String name) {
    return new File(dataFolder, name);
  }


  private void copy(String name) {
    if (!dataFolder.exists()) {
      dataFolder.mkdir();
    }

    File file = getFile(name);

    if (!file.exists()) {
      try (InputStream in = getClass().getResourceAsStream(name)) {
        Files.copy(in, file.toPath());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
