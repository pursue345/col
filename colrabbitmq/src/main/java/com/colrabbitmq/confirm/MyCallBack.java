package com.colrabbitmq.confirm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * 同时配置 交换机发布确认或消息队列回退  与  备份交换机时，备份交换机优先级高
 */
@Slf4j
@Component
public class MyCallBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnsCallback {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    //注入RabbitTemplate,虽然实现了RabbitTemplate接口，但调用它时，需要将容器里的引用指向我们实现的组件
    @PostConstruct
    private void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }


    /**
     * 交换机确认回调方法
     * 1.发消息 交换机接收到了，回调
     *  1.1 correlationData 保存回调消息的id及相关信息
     *  1.2 交换机接收到消息 ack = true
     *  1.3 cause null
     * 2.发消息 交换机接收失败了，回调
     *  2.1 correlationData 保存回调消息的id及相关信息
     *  2.2 交换机接收到消息 ack = false
     *  2.3 cause 失败的原因
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData!=null?correlationData.getId():"";
        if(ack){
            log.info("交换机已经收到ID:{}的消息",id);
        }else{
            log.info("交换机已经收到ID:{}的消息，由于原因：{}",id,cause);
        }
    }


    /**
     * 路由失败回调
     * 当消息传递过程中不可达目的地时消息返回给生产者,消息未从路由成功发送到队列会走这个回调
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        log.error("消息：{}，被交换机{}退回，退回原因：{}，路由key:{}",new String(returnedMessage.getMessage().getBody()),returnedMessage.getExchange(),returnedMessage.getReplyText(),returnedMessage.getRoutingKey());
    }

}
