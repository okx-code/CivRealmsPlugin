package com.civrealms.plugin.common.rabbit;

import com.civrealms.plugin.common.packet.DataSender;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class RabbitSender implements DataSender {

  private final ConcurrentNavigableMap<Long, ConfirmListener> outstandingConfirms = new ConcurrentSkipListMap<>();
  private final Channel channel;

  public RabbitSender(Channel channel) {
    this.channel = channel;

    init();
  }

  private void init() {
    try {
      channel.exchangeDeclare(RabbitClient.EXCHANGE_NAME, BuiltinExchangeType.DIRECT, false, false, null);
      channel.confirmSelect();
      channel.addConfirmListener(
          (deliveryTag, multiple) -> handleConfirms(deliveryTag, multiple, true),
          (deliveryTag, multiple) -> handleConfirms(deliveryTag, multiple, false));
      channel.addReturnListener((replyCode, replyText, exchange, routingKey, properties, body) -> {
        if (replyCode == 312) {
          // no listener so don't confirm it
          long no = (long) properties.getHeaders().get("no");
          ConfirmListener remove = outstandingConfirms.remove(no);
          handleConfirm(remove, false);
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleConfirms(long id, boolean multiple, boolean success) {
    if (multiple) {
      ConcurrentNavigableMap<Long, ConfirmListener> confirmed = outstandingConfirms
          .headMap(id, true);
      for (ConfirmListener value : confirmed.values()) {
        handleConfirm(value, success);
      }
      confirmed.clear();
    } else {
      ConfirmListener remove = outstandingConfirms.remove(id);
      handleConfirm(remove, success);
    }
  }

  private void handleConfirm(ConfirmListener listener, boolean success) {
    if (listener != null) {
      if (success) {
        listener.success();
      } else {
        listener.fail();
      }
    }
  }

  @Override
  public void send(String destination, byte[] bytes, Runnable success, Runnable fail) {
    try {
      System.out.println("Confirming out to " + destination);
      long no = channel.getNextPublishSeqNo();
      outstandingConfirms.put(no, ConfirmListener.create(success, fail));
      Map<String, Object> map = new HashMap<>();
      map.put("no", no);
      channel.basicPublish(RabbitClient.EXCHANGE_NAME, destination, true,
          MessageProperties.MINIMAL_BASIC.builder().headers(map).build(), bytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void sendProxy(byte[] bytes, Runnable success, Runnable fail) {
    send("proxy", bytes, success, fail);
  }
}
