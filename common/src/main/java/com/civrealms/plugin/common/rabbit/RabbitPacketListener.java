package com.civrealms.plugin.common.rabbit;

import com.civrealms.plugin.common.packet.DataReceiver;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import java.io.IOException;

public class RabbitPacketListener {

  private final String server;
  private final Channel channel;
  private final DataReceiver dataReceiver;

  public RabbitPacketListener(String server, DataReceiver dataReceiver, Channel channel) {
    this.server = server;
    this.dataReceiver = dataReceiver;
    this.channel = channel;
    init();
  }

  private void init() {
    try {
      channel.exchangeDeclare(RabbitClient.EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, null);
      String queue = channel.queueDeclare().getQueue();

      channel.queueBind(queue, RabbitClient.EXCHANGE_NAME, server);
      channel.basicConsume(queue, false,
          (consumerTag, message) -> receive(message.getEnvelope().getDeliveryTag(), message.getBody()),
          consumerTag -> {});
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void receive(long tag, byte[] data) throws IOException {
    boolean b;
    try {
      b = dataReceiver.receivePacket(data);
    } catch (Exception e) {
      e.printStackTrace();
      b = false;
    }
    if(b) {
      channel.basicAck(tag, false);
    } else {
      channel.basicNack(tag, false, false);
    }
  }
}
