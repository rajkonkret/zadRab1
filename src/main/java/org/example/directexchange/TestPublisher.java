package org.example.directexchange;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.example.CommonConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TestPublisher {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection(CommonConfig.AMQP_URL);
        Channel channel = connection.createChannel();

// ctrl / - komentarz
//        String message = "Turn on homeAppliance";
//        channel.basicPublish("my-direct-exchange", "homeAppliance", null, message.getBytes());
//        channel.basicPublish("", "ACQ", null, message.getBytes()); // wysłane bezpośrednio do kolejki

        String message = "Turn on personalDevice";
        channel.basicPublish("my-direct-exchange", "personalDevice", null, message.getBytes());
        channel.close();
        connection.close();
    }
}
