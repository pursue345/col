package com.colrabbitmq.util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;


public class RabbitMqUtils {

    public static Channel getChannel() throws Exception {
    //创建一个连接工厂
        ConnectionFactory connectionFactory = new ConnectionFactory();
        //工厂ip 连接Rabbitmq的队列
        connectionFactory.setHost("192.168.3.28");
        //用户名
        connectionFactory.setUsername("guest");
        //密码
        connectionFactory.setPassword("guest");
        //创建连接
        Connection connection = connectionFactory.newConnection();
        //获取信道
        Channel channel = connection.createChannel();
        return channel;
    }
}
