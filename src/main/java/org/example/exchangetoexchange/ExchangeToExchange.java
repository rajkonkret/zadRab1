package org.example.exchangetoexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.example.ConnectionManager;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ExchangeToExchange {

    public static void declareExchanges() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.exchangeDeclare("linked-direct-exchange", BuiltinExchangeType.DIRECT, true);
        channel.exchangeDeclare("home-direct-exchange", BuiltinExchangeType.DIRECT, true);

        channel.close();
    }

    public static void declareQueues() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.queueDeclare("MobileQ", true, false, false, null);
        channel.queueDeclare("ACQ", true, false, false, null);
        channel.queueDeclare("LightQ", true, false, false, null);

        channel.queueDeclare("FanQ", true, false, false, null);
        channel.queueDeclare("LaptopQ", true, false, false, null);

        channel.close();
    }

    public static void declareQueueBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.queueBind("MobileQ", "linked-direct-exchange", "health.*");
        channel.queueBind("ACQ", "linked-direct-exchange", "#.sports.*");
        channel.queueBind("LightQ", "linked-direct-exchange", "#.education");

        channel.close();
    }
}
