package com.colrabbitmq.ttlrabbitmq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class DeadLetterQueueConsumer {

    //接收消息
    @RabbitListener(queues = "QD")
    public void resiveD(Message message){
        String msg = new String(message.getBody());
        log.info("当前时间：{}，收到死信队列的消息：{}",new Date().toString(),msg);
    }

    //接收消息，基于插件的
//    @RabbitListener(queues = DelayedQueueConfig.DELAYED_QUEUE_NAME)
//    public void resiveDelayQueue(Message message){
//        String msg = new String(message.getBody());
//        log.info("当前时间：{}，收到死信队列的消息：{}",new Date().toString(),msg);
//    }
}
