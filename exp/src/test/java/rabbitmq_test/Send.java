package rabbitmq_test;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.util.concurrent.TimeoutException;

/**
 * 测试rabbitmq发送
 * 192.168.185.126
 * 123456
 */

public class Send {
    private final static String QUEUE_NAME = "onlysend";

    public static void main(String[] argv) throws java.io.IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.185.126");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin#123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        String message = "zjs!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");

        channel.close();
        connection.close();
    }
}
