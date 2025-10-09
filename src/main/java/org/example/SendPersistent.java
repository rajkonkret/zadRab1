package org.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SendPersistent {

    private final static String QUEUE_NAME = "persist_queue";

    public static void main(String[] args) throws IOException {

        Channel channel = ConnectionManager.getConnection().createChannel();

        boolean durable = true;
        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);

        String message = "Persist Messaage";
        channel.basicPublish("",
                QUEUE_NAME,
                MessageProperties.PERSISTENT_TEXT_PLAIN,  // delivery_mode:	2
//                MessageProperties.TEXT_PLAIN, // delivery_mode:	1
                message.getBytes(StandardCharsets.UTF_8));

        System.out.println("Wysłano:" + message);

    }
}
