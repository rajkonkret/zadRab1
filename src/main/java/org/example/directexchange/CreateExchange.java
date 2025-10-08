package org.example.directexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.example.CommonConfig;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class CreateExchange {
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        Connection connection = factory.newConnection(CommonConfig.AMQP_URL);
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("my-direct-exchange", BuiltinExchangeType.DIRECT, true);

        channel.close();
        connection.close();
    }
}
