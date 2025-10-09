package org.example.alternateexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.example.ConnectionManager;
import org.example.topicexchange.TopicExchange;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class AlternateExchange {

    public static void declareExchange() throws IOException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.exchangeDeclare("alt.fanout.exchange", BuiltinExchangeType.FANOUT, true);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("alternate-exchange", "alt.fanout.exchange");
        channel.exchangeDeclare("alt.topic.exchange", BuiltinExchangeType.TOPIC, true, false, arguments);
    }

    public static void declareQueues() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.queueDeclare("HealthQ", true, false, false, null);
        channel.queueDeclare("SportsQ", true, false, false, null);
        channel.queueDeclare("EducationQ", true, false, false, null);

        channel.queueDeclare("FaultQ", true, false, false, null);

        channel.close();
    }

    public static void declareBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.queueBind("HealthQ", "alt.topic.exchange", "health.*");
        channel.queueBind("SportsQ", "alt.topic.exchange", "#.sports.*");
        channel.queueBind("EducationQ", "alt.topic.exchange", "#.education");

        channel.queueBind("FaultQ", "alt.fanout.exchange", "");

        channel.close();
    }

    public static void publishMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        String message = "Direct message - education.health\"";
        channel.basicPublish("alt.topic.exchange", "education.health", null, message.getBytes());

        message = "Direct message - education";
        channel.basicPublish("alt.topic.exchange", "education", null, message.getBytes());

        message = "Direct message - sports.sports";
        channel.basicPublish("alt.topic.exchange", "sports.sports", null, message.getBytes());

        channel.close();

    }

    private static void subscribeMessages() throws IOException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.basicConsume("HealthQ", true, ((consusmerTag, message) -> {
            System.out.println("HealthQ Queue");
            System.out.println(consusmerTag);
            System.out.println("HealthQ: " + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("SportsQ", true, ((consusmerTag, message) -> {
            System.out.println("SportsQ Queue");
            System.out.println(consusmerTag);
            System.out.println("SportsQ: " + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("EducationQ", true, ((consusmerTag, message) -> {
            System.out.println("EducationQ Queue");
            System.out.println(consusmerTag);
            System.out.println("EducationQ: " + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

        channel.basicConsume("FaultQ", true, ((consusmerTag, message) -> {
            System.out.println("FaultQ Queue");
            System.out.println(consusmerTag);
            System.out.println("FaultQ: " + new String(message.getBody()));
            System.out.println(message.getEnvelope());
        }), consumerTag -> {
            System.out.println(consumerTag);
        });

    }

    public static void main(String[] args) throws IOException, TimeoutException {
        AlternateExchange.declareQueues();
        AlternateExchange.declareExchange();
        AlternateExchange.declareBindings();

        Thread subscribe = new Thread() {
            @Override
            public void run() {
                try {
                    AlternateExchange.subscribeMessages();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    AlternateExchange.publishMessage();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

        subscribe.start();
        publish.start();
    }
}
