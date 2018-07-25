package rabbitmq_test;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 测试rabbitmq发送
 *           |            RabbitMQ          |
 *           |            routes            |
 * provider--|-->交换机(Exchange)---->队列---|-->消费者
 *
 * 192.168.185.126
 * 123456
 */

public class Send {
    private final static String QUEUE_NAME = "onlysend";
    private final static String EXCHANGE_NAME = "onlysend_exchange_demo";

    /**
     * 队列相关设定
     * @return
     */
    public static  Map<String, Object> create_arguments(){
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-message-ttl", 10000); //消息生存时间,不设定则不会删除,这里统一设置为10秒
        arguments.put("x-max-length", 4); //指定队列长度，不指定则无限长，这里设定为4，超过则删除前面的消息
        arguments.put("x-max-length-bytes", 1024); //指定队列存储消息的占用空间大小，到最大的时候会把前面的消息删除
        arguments.put("x-max-priority", 10); //设置消息的优先级，不设定则无优先级，设定后优先级值越大，越提前呗消费。
        /**
         * 当队列中的消息过期，或因异常被丢弃则可以设置推送到其他交换机和队列中,让其他消费者消费
         */
        arguments.put("x-dead-letter-exchange", "对应的交换机"); //当队列消息长度大于最大长度、或者过期的等，将从队列中删除的消息推送到指定的交换机中去而不是丢弃掉,Features=DLX
        arguments.put("x-dead-letter-routing-key", "交换机对应的路由键"); //将删除的消息推送到指定交换机的指定路由键的队列中去, Feature=DLK
        return arguments;
    }

    /**
     * 获取队列相关信息并放回
     * @return
     */
    public static String getQueryMsg(Channel channel)throws java.io.IOException{
        /**
         * 队列相关信息
         */
        //获取队列名
        String queueName = channel.queueDeclare().getQueue();
        //获取消息数
        int megCount = channel.queueDeclare().getMessageCount();
        //获取消费者数
        int csrCount = channel.queueDeclare().getConsumerCount();
        String msg = "队列名:" + queueName + "消息数:" + megCount + "消费者数: " +  csrCount;
        return msg;
    }

    /**
     * 设定连接对应参数
     * @param factory
     * @return
     */
    public static ConnectionFactory connConfig(ConnectionFactory factory){
        factory.setHost("192.168.185.126");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin#123");
        return factory;
    }


    //通过交换机转发
    /**
     * 扇形交换机(广播交换机)：Fanout exchange
     *         最基本的交换机类型
     *         扇形交换机会把能接收到的消息全部发送给绑定在自己身上的队列。
     *         因为广播不需要“思考”，所以扇形交换机处理消息的速度也是所有的交换机类型里面最快的。
     *
     *                            广播
     *                           -----> 队列1
     *         消息---->扇形交换机 -----> 队列2
     *                           -----> 队列3
     *
     * 直连交换机：Direct exchange
     *         直连交换机是一种带路由功能的交换机
     *         一个队列会和一个交换机绑定，除此之外再绑定一个routing_key,不同队列可以绑定一个key
     *         适用场景：有优先级的任务，根据任务的优先级把消息发送到对应的队列，这样可以指派更多的资源去处理高优先级的队列。
     *
     *                            routing_key=key1
     *                           ------------------>队列1
     *                            routing_key=key1
     *         消息---->直连交换机------------------>队列2
     *                            routing_key=key2
     *                           ------------------>队列3
     *                            routing_key=key3
     *                           ------------------>队列4
     * 主题交换机：Topic exchange
     *          直连交换机的routing_key方案非常简单，如果我们希望一条消息发送给多个队列，那么这个交换机需要绑定上非常多的routing_key，
     *          假设每个交换机上都绑定一堆的routing_key连接到各个队列上。那么消息的管理就会异常地困难。
     *          所以RabbitMQ提供了一种主题交换机，发送到主题交换机上的消息需要携带指定规则的routing_key，
     *          主题交换机会根据这个规则将数据发送到对应的(多个)队列上。
     *          主题交换机的routing_key需要有一定的规则，交换机和队列的binding_key需要采用*.#.*.....的格式，每个部分用.分开，其中：
     *           *表示一个单词
     *           #表示任意数量（零个或多个）单词。
     *           假设有一条消息的routing_key为fast.rabbit.white,那么带有这样binding_key的几个队列都会接收这条消息：

     *           fast..
     *           ..white
     *           fast.#
     *           ……
     * 首部交换机：Headers exchange
     *
     * 首部交换机是忽略routing_key的一种路由方式。路由器和交换机路由的规则是通过Headers信息来交换的，这个有点像HTTP的Headers。
     * 将一个交换机声明成首部交换机，绑定一个队列的时候，定义一个Hash的数据结构，
     * 消息发送的时候，会携带一组hash数据结构的信息，当Hash的内容匹配上的时候，消息就会被写入队列。
     *
     * 绑定交换机和队列的时候，Hash结构中要求携带一个键“x-match”，这个键的Value可以是any或者all，、
     * 这代表消息携带的Hash是需要全部匹配(all)，还是仅匹配一个键(any)就可以了。
     * 相比直连交换机，首部交换机的优势是匹配的规则不被限定为字符串(string)。
     *
     */
    public static void Directexchange(Connection connection,String exchangeName,String type,String routingKey) throws java.io.IOException{
        String queryname = type+"Demo";

        //ConnectionFactory：构造Connection实例
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(exchangeName, type); //direct fanout topic
        channel.queueDeclare(type+"Demo", false, false, false, null);
        byte[] messageBodyBytes = "hello world".getBytes();
        if (type.equals("direct")){
            /**
             * Direct Exchange – 处理路由键。需要将一个队列绑定到交换机上，要求该消息与一个特定的路由键完全匹配。
             * 这是一个完整的匹配。如果一个队列绑定到该交换机上要求路由键 “dog”，则只有被标记为“dog”的消息才被转发，
             * 不会转发dog.puppy，也不会转发dog.guard，只会转发dog。
             */
            channel.queueBind("queueName", exchangeName, routingKey);
            //需要绑定路由键 routingKey
            channel.basicPublish("exchangeName", routingKey, null, messageBodyBytes);
        }else if (type.equals("fanout")){
            /**
             * Fanout Exchange – 不处理路由键。你只需要简单的将队列绑定到交换机上。
             * 一个发送到交换机的消息都会被转发到与该交换机绑定的所有队列上。
             * 很像子网广播，每台子网内的主机都获得了一份复制的消息。Fanout交换机转发消息是最快的。
             */
            channel.queueBind("queueName", exchangeName, routingKey);

            //这里再新赠一个队列
            channel.queueDeclare(type+"Demo"+1, false, false, false, null);
            channel.queueBind("queueName1", "exchangeName", "routingKey1");

            //路由键需要设置为空,路由会同事转发给两个队列
            channel.basicPublish(exchangeName, "", null, messageBodyBytes);
        }else if(type.equals("topic")){
            /**
             * Topic Exchange – 将路由键和某模式进行匹配。此时队列需要绑定要一个模式上。符号“#”匹配一个或多个词，
             * 符号“*”匹配不多不少一个词。因此“audit.#”能够匹配到“audit.irs.corporate”，但是“audit.*” 只会匹配到“audit.irs”。
             */
            channel.queueBind("queueName", exchangeName, routingKey+".*");
            channel.basicPublish("exchangeName", routingKey+".one", null, messageBodyBytes);
        }
    }



    public static void main(String[] argv) throws java.io.IOException, TimeoutException {
        //ConnectionFactory：构造Connection实例
        ConnectionFactory factory = new ConnectionFactory();
        factory = connConfig(factory);
        //创建连接
        Connection connection = factory.newConnection();
        //Channel：表示AMQP 0-9-1通道，并提供大部分操作（协议方法）。v
        //createChannel(): 表示打开一个通道
        Channel channel = connection.createChannel();

        Map<String, Object> arguments = create_arguments();

        /**交换器声明
         * 参数1： 交换器名
         * 参数2： 交换器类型 Direct、Fanout、Topic
         * 参数3：
         */
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true, false, null);


        /**队列声明
         * queueDeclare(String queue,  队列名称
         boolean durable, 队列是否持久化（设置消息持久化必须先设置队列持久化以及交换机持久化）
         boolean exclusive, 是否排外的,关闭则两个消费者都能访问，如果是排外的则给当前队列加锁，其他通道不能访问
         Map<String, Object> arguments);
         */
        channel.queueDeclare(QUEUE_NAME, false, false, false, arguments);

        /**
        //单条信息直接写入队列
        String message = "zjs!";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
        System.out.println(" [x] Sent '" + message + "'");
         */
         //模拟批量信息写入队列
        for(int i = 0; i<5 ; i++){
             String message = "zjs-message-" + i;
            /**
             BasicProperties 消息属性
             private String contentType; 消息内容类型, exp: 使用文本 text/plain 使用html text/html 使用json application/json
             private String contentEncoding;  消息内容编码 exp: utf-8
             private Map<String, Object> headers;
             private Integer deliveryMode; 消息是否持久化 non-persistent (1) or persistent (2)
             private Integer priority; 消息优先级。 值: 0~9
             private String correlationId;
             private String replyTo; 指定callback queue的名字
             private String expiration;
             private String messageId;
             private Date timestamp;
             private String type;
             private String userId;
             private String appId;
             private String clusterId;
             */
             channel.basicPublish("", QUEUE_NAME, new AMQP.BasicProperties.Builder()
                     .correlationId(java.util.UUID.randomUUID().toString()).build(), message.getBytes());
             channel.basicPublish("", QUEUE_NAME, null, message.getBytes());
             System.out.println(" [x] Sent '" + message + "'");
         }

        //断开连接
        channel.close();
        connection.close();
    }
}
