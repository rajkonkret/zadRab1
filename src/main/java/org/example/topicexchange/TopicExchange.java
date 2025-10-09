package org.example.topicexchange;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.example.ConnectionManager;
import org.example.fanoutexchnge.FanotExchange;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class TopicExchange {
    public static void declareExchange() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.exchangeDeclare("my-topic-exchange", BuiltinExchangeType.TOPIC, true);

        channel.close();
    }

    public static void declareBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.queueBind("HealthQ", "my-topic-exchange", "health.*");
        channel.queueBind("SportsQ", "my-topic-exchange", "#.sports.*");
        channel.queueBind("EducationQ", "my-topic-exchange", "#.education");

        channel.close();
    }

    public static void declareQueues() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.queueDeclare("HealthQ", true, false, false, null);
        channel.queueDeclare("SportsQ", true, false, false, null);
        channel.queueDeclare("EducationQ", true, false, false, null);

        channel.close();
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        TopicExchange.declareQueues();
        TopicExchange.declareExchange();
        TopicExchange.declareBindings();

        Thread subscribe = new Thread() {
            @Override
            public void run() {
                try {
                    TopicExchange.subscribeMessages();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    TopicExchange.publishMessage();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

        subscribe.start();
        publish.start();
    }

    private static void publishMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        String message = "Drink a lot of Water and stay Healthy";
//        channel.basicPublish("my-topic-exchange", "health.education", null, message.getBytes());
//
//        message = "Learn somthing new everyday";
//        channel.basicPublish("my-topic-exchange", "education", null, message.getBytes());

//        message = "Stay fit in Mind and Body"; // wiadomosc nie trafi do kolejki
//        channel.basicPublish("my-topic-exchange", "education.health", null, message.getBytes());
//
//        message = "Just do it!";
//        channel.basicPublish("my-topic-exchange", "sports.sports", null, message.getBytes());
//
//        message = "Just do it!";
//        channel.basicPublish("my-topic-exchange", "sports.sports", null, message.getBytes());

        message = "Just do it!"; // 	#.sports.* # sports.sports  * sports
        channel.basicPublish("my-topic-exchange", "sports.sports.sports.sports", null, message.getBytes());

        message = "Just do it!"; // 	#.sports.* # sports.sports  * sports
        // # - dowolna ilośc wyrazów
        // * - dokładnie jeden wyraz
        channel.basicPublish("my-topic-exchange", "sports.sports.sports.sports.abc", null, message.getBytes());

        message = "Just do it!"; // 	#.sports.* # sports.sports  * sports
        // # - dowolna ilośc wyrazów
        // * - dokładnie jeden wyraz
        // nie zadziała bo po sports nie ma wyrazu, * wymaga dokładnie jeden wyraz
        channel.basicPublish("my-topic-exchange", "sports", null, message.getBytes());

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

    }
}
