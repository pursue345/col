package com.colseckill.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MqSender {

    @Autowired
    RabbitTemplate rabbitTemplate;

    public void sendMessage(Object msg){
        System.out.println("发送消息");
        rabbitTemplate.convertAndSend("queue",msg);
    }

    public void sendseckillQueue(Object msg){
        System.out.println("发送消息");
        rabbitTemplate.convertAndSend("seckillQueue",msg);
    }
}
