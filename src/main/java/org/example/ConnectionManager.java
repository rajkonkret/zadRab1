package org.example;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConnectionManager {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                ConnectionFactory factory = new ConnectionFactory();
                connection = factory.newConnection(CommonConfig.AMQP_URL);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }
}
