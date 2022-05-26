package com.colrabbitmq.ttlrabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/ttl")
public class SendMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    //开始发消息
    @RequestMapping("/sendMsg/{message}")
    public void sendMsg(@PathVariable String message){
        String ttlTime = "20000";
        log.info("发送一条消息给三个TTL队列");
        rabbitTemplate.convertAndSend("X","XA","消息来自ttl为10s的队列"+message);
        rabbitTemplate.convertAndSend("X","XB","消息来自ttl为40s的队列"+message);
        //生产者发送过期时间
        rabbitTemplate.convertAndSend("X","XC","消息来自ttl为"+ttlTime+"ms的队列"+message,msg->{
            //发送消息时，延迟时长
            msg.getMessageProperties().setExpiration(ttlTime);
            return msg;
        });
    }

    //开始发消息,基于插件的 消息及延迟的时间
//    @RequestMapping("/sendDelayMsg/{message}/{delayTime}")
//    public void sendMsg(@PathVariable String message,@PathVariable Integer delayTime){
//        log.info("当前时间：{}，发送时长：{},给延迟队列delayed.queue：{}", new Date(),delayTime,message);
//        rabbitTemplate.convertAndSend(DelayedQueueConfig.DELAYED_EXCHANGE_NAME,DelayedQueueConfig.DELAYED_ROUTING_KEY,"消息来自ttl为"+delayTime+"ms的队列"+message,msg->{
//            //发送消息时，延迟时长
//            msg.getMessageProperties().setDelay(delayTime);
//            return msg;
//        });
//    }
}
