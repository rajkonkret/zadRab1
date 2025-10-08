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
                    e.printStackTrace();
                }
            }
        };

        Thread publish = new Thread() {
            @Override
            public void run() {
                try {
                    TopicExchange.publishMessage();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    private static void publishMessage() {
    }

    private static void subscribeMessages() {
    }
}
