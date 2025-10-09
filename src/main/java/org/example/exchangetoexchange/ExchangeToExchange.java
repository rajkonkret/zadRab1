package org.example.exchangetoexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.example.ConnectionManager;
import org.example.headersexchange.HeadersExchange;

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

        channel.queueBind("MobileQ", "linked-direct-exchange", "personalDevice");
        channel.queueBind("ACQ", "linked-direct-exchange", "homeAppliance");
        channel.queueBind("LightQ", "linked-direct-exchange", "homeAppliance");

        channel.queueBind("FanQ", "home-direct-exchange", "homeAppliance");
        channel.queueBind("LaptopQ", "home-direct-exchange", "personalDevice");

        channel.close();
    }

    public static void declareExchangeBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        // docelowy, zródłowy
        channel.exchangeBind("linked-direct-exchange", "home-direct-exchange", "homeAppliance");

        channel.close();
    }

    private static void subscribeMessages() throws IOException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.basicConsume("MobileQ", true, ((consusmerTag, message) -> {
            System.out.println("MobileQ Queue");
            System.out.println(consusmerTag);
            System.out.println("MobileQ: " + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("ACQ", true, ((consusmerTag, message) -> {
            System.out.println("ACQ Queue");
            System.out.println(consusmerTag);
            System.out.println("ACQ: " + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("LightQ", true, ((consusmerTag, message) -> {
            System.out.println("LightQ Queue");
            System.out.println(consusmerTag);
            System.out.println("LightQ: " + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("FanQ", true, ((consusmerTag, message) -> {
            System.out.println("FanQ Queue");
            System.out.println(consusmerTag);
            System.out.println("FanQ: " + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("LaptopQ", true, ((consusmerTag, message) -> {
            System.out.println("LaptopQ Queue");
            System.out.println(consusmerTag);
            System.out.println("LaptopQ: " + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

    }

    public static void publishMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        String message = "Direct message - homeAppliance";
        channel.basicPublish("home-direct-exchange", "homeAppliance", null, message.getBytes());

        channel.close();

    }

    public static void main(String[] args) throws IOException, TimeoutException {
        ExchangeToExchange.declareQueues();
        ExchangeToExchange.declareExchanges();
        ExchangeToExchange.declareQueueBindings();
        ExchangeToExchange.declareExchangeBindings();

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    ExchangeToExchange.publishMessage();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread subscribe = new Thread() {
            @Override
            public void run() {
                try {
                    ExchangeToExchange.subscribeMessages();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        publish.start();
        subscribe.start();
    }
}
