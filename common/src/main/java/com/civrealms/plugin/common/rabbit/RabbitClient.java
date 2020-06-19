package com.civrealms.plugin.common.rabbit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class RabbitClient {

  public static final String EXCHANGE_NAME = "civtest";

  public Supplier<Channel> getChannels() {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setUsername("guest");
    factory.setPassword("guest");
    factory.setHost("localhost");
    try {
      Connection connection = factory.newConnection();
      return () -> {
        try {
          Channel channel = connection.createChannel();
          channel.addShutdownListener(cause -> {
            System.out.println("Chanel shutdown: ");
            cause.printStackTrace();
          });
          return channel;
        } catch (IOException e) {
          e.printStackTrace();
          return null;
        }
      };
    } catch (IOException | TimeoutException e) {
      e.printStackTrace();
      return null;
    }
  }
}
