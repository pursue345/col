package com.colrabbitmq.basicOpe;

import com.colrabbitmq.util.RabbitMqUtils;
import com.rabbitmq.client.*;

public class ConsumerRabbitmq {

    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws Exception{
        //获取信道
        Channel channel = RabbitMqUtils.getChannel();

        //声明
        DeliverCallback deliverCallback = (consumerTag,message)->{
            System.out.println("接收到的消息：" + new String(message.getBody()));
        };
        //取消消息时的回调
        CancelCallback cancelCallback = consumerTag->{
            System.out.println("消息取消消费接口回调逻辑");
        };

        //设置分发 1-不公平分发，0-公平分发
        channel.basicQos(1);
        /**
         * 消费消费消息
         * 1.消费哪个队列
         * 2.消费成功之后是否要自动应答，true-自动应答，false-手动应答
         * 3.消费者未成功消费的回调
         * 4.消费者取消消费的回调
         */
        channel.basicConsume(QUEUE_NAME,true,deliverCallback,cancelCallback);
    }
}
