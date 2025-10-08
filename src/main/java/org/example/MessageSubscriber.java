package org.example;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class MessageSubscriber {
    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.printf("Hello and welcome!");

        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection(CommonConfig.AMQP_URL);
        Channel channel = connection.createChannel();

        DeliverCallback deliverCallback = (s, delivery) -> {
            System.out.println(new String(delivery.getBody(), StandardCharsets.UTF_8));
        };

        CancelCallback cancelCallback = s -> {
            System.out.println();
        };

        channel.basicConsume(CommonConfig.DEFAULT_QUEUE, true, deliverCallback, cancelCallback);
    }
}
