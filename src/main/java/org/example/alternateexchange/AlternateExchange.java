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

    public static void main(String[] args) throws IOException, TimeoutException {
        AlternateExchange.declareQueues();
        AlternateExchange.declareExchange();
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
}
