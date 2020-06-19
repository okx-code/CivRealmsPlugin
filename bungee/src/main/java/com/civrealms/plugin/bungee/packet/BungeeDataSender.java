//package com.civrealms.plugin.bungee.packet;
//
//import com.civrealms.plugin.bungee.DestinationManager;
//import com.civrealms.plugin.common.packet.DataSender;
//import com.civrealms.plugin.common.packets.stream.DataOutputStream;
//
//public class BungeeDataSender implements DataSender {
//  private final DestinationManager destinationManager;
//
//  public BungeeDataSender(DestinationManager destinationManager) {
//    this.destinationManager = destinationManager;
//  }
//
//  @Override
//  public void send(String destination, byte[] bytes) {
//    DataOutputStream out = new DataOutputStream();
//    out.write(bytes);
//    destinationManager.getServer(destination).sendData("CR_DATA", out.toByteArray());
//  }
//
//  @Override
//  public void sendProxy(byte[] data) {
//    throw new UnsupportedOperationException("Cannot send message from proxy to proxy");
//  }
//}
