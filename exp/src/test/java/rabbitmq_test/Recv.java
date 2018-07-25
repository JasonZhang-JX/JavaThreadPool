package rabbitmq_test;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Recv {
    private final static String QUEUE_NAME = "onlysend";

    public static void main(String[] argv) throws java.io.IOException, TimeoutException {
        /**
         * 新增的DefaultConsumer类是Consumer接口的实现,我们使用它来接收server推送来的消息。
         * 起始的代码和sender差不多（译注：都是样板代码）：我们创建连接，打开channel，并且声明我们要监听的队列。
         * 注意这个队列要与Send类要发送的队列一致。
         *
         */
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.185.126");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin#123");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //队列查询
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        //定义每次从队列中获取的数量
        channel.basicQos(1);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                //获取包头信息,比如获取correlationId
                String correlationId =  properties.getCorrelationId();

                System.out.println(" [x] Received '" + message + "'  " + "correlationId: " + correlationId);
            }
        };
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
