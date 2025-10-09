package org.example.headersexchange;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import org.example.ConnectionManager;
import org.example.topicexchange.TopicExchange;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.TimeoutException;

public class HeadersExchange {
    public static void declareExchange() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        // true - durable, trwały
        channel.exchangeDeclare("my-header-exchange", BuiltinExchangeType.HEADERS, true);

        channel.close();
    }

    public static void declareQueues() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        channel.queueDeclare("HealthQ", true, false, false, null);
        channel.queueDeclare("SportsQ", true, false, false, null);
        channel.queueDeclare("EducationQ", true, false, false, null);
        channel.queueDeclare("VercomQ", true, false, false, null);

        channel.close();
    }

    public static void declareBindings() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        // tworzenie linków (bindingów)
        // {"arg" : "wartość"}
        Map<String, Object> healthArgs = new HashMap<>();
        healthArgs.put("x-match", "any"); // any - dowalny
        healthArgs.put("h1", "Header1");
        healthArgs.put("h2", "Header2");
        channel.queueBind("HealthQ", "my-header-exchange", "", healthArgs);

        Map<String, Object> sportsArgs = new HashMap<>();
        sportsArgs.put("x-match", "all"); // all - wszystkie
        sportsArgs.put("h1", "Header1");
        sportsArgs.put("h2", "Header2");
        channel.queueBind("SportsQ", "my-header-exchange", "", sportsArgs);

        Map<String, Object> educationArgs = new HashMap<>();
        educationArgs.put("x-match", "any"); //
        educationArgs.put("h1", "Header1");
        educationArgs.put("h2", "Header2");
        channel.queueBind("EducationQ", "my-header-exchange", "", educationArgs);

        Map<String, Object> vercomArgs = new HashMap<>();
        vercomArgs.put("x-match", "any"); //
        vercomArgs.put("h3", "Header3");
        channel.queueBind("VercomQ", "my-header-exchange", "", vercomArgs);

        channel.close();
    }

    public static void publishMessage() throws IOException, TimeoutException {
        Channel channel = ConnectionManager.getConnection().createChannel();

        String message = "Headers exchange example 1";
        Map<String, Object> headerMap = new HashMap<>();
        headerMap.put("h1", "Header1");
        headerMap.put("h2", "Header2");
        AMQP.BasicProperties properties = new AMQP.BasicProperties().builder()
                .headers(headerMap)
                .build();

        channel.basicPublish("my-header-exchange", "", properties, message.getBytes());
        message = "Headers exchange example 2 h2";
        Map<String, Object> headerMap2 = new HashMap<>();
        headerMap2.put("h2", "Header2");

       properties = new AMQP.BasicProperties().builder()
                .headers(headerMap2)
                .build();

        channel.basicPublish("my-header-exchange", "", properties, message.getBytes());


        message = "Headers exchange example 3 h3";
        Map<String, Object> headerMap3 = new HashMap<>();
        headerMap2.put("h3", "Header2");

        properties = new AMQP.BasicProperties().builder()
                .headers(headerMap3)
                .build();

        channel.basicPublish("my-header-exchange", "", properties, message.getBytes());

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
    public static void main(String[] args) throws IOException, TimeoutException {
        HeadersExchange.declareQueues();
        HeadersExchange.declareExchange();
        HeadersExchange.declareBindings();

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    HeadersExchange.publishMessage();
                } catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread subscribe = new Thread() {
            @Override
            public void run() {
                try {
                    HeadersExchange.subscribeMessages();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        publish.start();
        subscribe.start();
    }
}
