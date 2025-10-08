package org.example;

import com.rabbitmq.client.ConnectionFactory;

public class MessagePublisher {

        // psvm tabulator
    public static void main(String[] args) {
        System.out.printf("Hello and welcome!");

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
    }
}
