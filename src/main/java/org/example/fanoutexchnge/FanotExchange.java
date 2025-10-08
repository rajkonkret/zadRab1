package org.example.fanoutexchnge;

import com.rabbitmq.client.Channel;
import org.example.ConnectionManager;

public class FanotExchange {

    public static void declareExchange() {
        Channel channel = ConnectionManager.getConnection().createChannel();
    }
    public static void main(String[] args) {



    }
}
