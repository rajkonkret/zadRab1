package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class MessagePublisher {

    // psvm tabulator
    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.printf("Hello and welcome!");

        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection(CommonConfig.AMQP_URL);
        Channel channel = connection.createChannel();

        channel.queueDeclare(CommonConfig.DEFAULT_QUEUE, true, false, false, null);

        for (int i = 0; i < 4; i++) {
            String message = "Getting - MSQ" + i;
            channel.basicPublish("", CommonConfig.DEFAULT_QUEUE, null, message.getBytes());
        }

        channel.close();
        connection.close();
    }
}
