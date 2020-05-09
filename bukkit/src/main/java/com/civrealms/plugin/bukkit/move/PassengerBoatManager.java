package com.civrealms.plugin.bukkit.move;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;

public class PassengerBoatManager {
  private final Map<UUID, WeakReference<Boat>> boatMap = new HashMap<>();
  private final Map<UUID, Player> boatWaiters = new WeakHashMap<>();

  public void addDriver(UUID boatId, Boat boat) {
    boatMap.put(boatId, new WeakReference<>(boat));
    Player waiter = boatWaiters.get(boatId);
    if (waiter != null) {
      boat.addPassenger(waiter);
    }
  }

  public boolean addPassenger(UUID boatId, Player passenger) {
    WeakReference<Boat> weakBoat = boatMap.get(boatId);
    if (weakBoat == null) {
      addPassengerWaitingForBoat(boatId, passenger);
      return false;
    }
    Boat boat = weakBoat.get();
    if (boat == null) {
      boatMap.remove(boatId);
      addPassengerWaitingForBoat(boatId, passenger);
      return false;
    }
    boat.addPassenger(passenger);
    return true;
  }

  private void addPassengerWaitingForBoat(UUID boatId, Player passenger) {
    boatWaiters.put(boatId, passenger);
  }
}
