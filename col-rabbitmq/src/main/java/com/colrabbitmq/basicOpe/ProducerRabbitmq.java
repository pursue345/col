package com.colrabbitmq.basicOpe;

import com.colrabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.*;

import java.util.HashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ProducerRabbitmq {

    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception{
        //获取信道
        Channel channel = RabbitMqUtils.getChannel();
        /**
         * 生成队列
         * 1.队列名称
         * 2.队列里面的消息是否持久化，默认存储在内存
         * 3.该队列是否只供一个消费者消费，是否进行消费共享，true可以多个消费者消费
         * 4.是否自动删除，最后一个消费者端开连接后，该队列是否直接删除，true-自动删除，false-不自动删除
         * 5.其它参数
         */
        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-max-priority",10);//官方允许0-255之间，此处设置优先级范围为0-10
        channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
        //发消息
//        String message = "hello world!!!";

        //开启发布确认
        channel.confirmSelect();

        /**
         * 线程安全有序的一个哈希表，适用于高并发的情况下
         * 1.轻松的将序号与消息关联
         * 2.轻松批量删除条目，只要给到序号
         * 3.支持高并发（多线程）
         */
        ConcurrentSkipListMap<Long,String> outstandingConfirm = new ConcurrentSkipListMap<>();

        //消息成功回调
        ConfirmCallback ackCllback = (deliveryTag,multiple)->{
            if(multiple){
                //删除已经确认的消息，剩下的就是未确认的消息
                ConcurrentNavigableMap<Long,String> confirmed = outstandingConfirm.headMap(deliveryTag);
                confirmed.clear();
            }else{
                outstandingConfirm.remove(deliveryTag);
            }

            System.out.println("确认的消息：" + deliveryTag);
        };
        //消息失败回调
        ConfirmCallback nackCallback = (deliveryTag,multiple)->{
            String messageStr = outstandingConfirm.get(multiple);
            System.out.println("未确认消息：" + messageStr + "未确认的消息tag：" + deliveryTag);
        };
        //准备消息的监听器，监听消息成功、失败
        channel.addConfirmListener(ackCllback,nackCallback);
        /**
         * 发送一个消费
         * 1.发送到那个交换机
         * 2.路由的key值是哪一个，本次是队列的名称
         * 3.其它参数信息 MessageProperties.PERSISTENT_TEXT_PLAIN 消息持久化
         * 4.发送消息的消息体
         */
        //设置生产者发送消息为持久化（要求保存在磁盘上）,保存在内存中
        for (int i = 1; i < 11; i++) {
            String message = "info" + i;
            if(i == 6){
                AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().priority(4).build();
                channel.basicPublish("",QUEUE_NAME, properties,message.getBytes());
            }else{
                channel.basicPublish("",QUEUE_NAME, null,message.getBytes());
            }
            //消息持久化
//            channel.basicPublish("",QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN,message.getBytes());
        }

        //消息发布确认
        boolean b = channel.waitForConfirms();
        System.out.println("发送消息完毕");
    }
}
