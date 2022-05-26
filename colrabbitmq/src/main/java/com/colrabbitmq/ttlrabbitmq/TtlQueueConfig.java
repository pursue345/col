package com.colrabbitmq.ttlrabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * TTL队列  配置文件类代码
 *
 * 一个生产者-》一个X交换机-》三个队列QA、QB、QC（延迟时间由生产者指定）,路由分别为XA、XB、XC-》一个死信交换Y-》一个死信队列QD-》一个消费者
 *
 * 死信队列原因
 * 1.消息被拒（Basic.Reject /Basic.Nack) 且 requeue = false。
 * 2.消息TTL过期。
 * 3.队列满了，无法再添加
 */
@Configuration
public class TtlQueueConfig {

    //普通交换机的名称
    public static final String X_EXCHANGE = "X";
    //死信交换机的名称
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    //普通队列名称
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    public static final String QUEUE_C = "QC";
    //死信队列的名称
    public static final String DEAD_LETTER_QUEUE = "QD";

    //声明xExchange 别名
    @Bean("xExchange")
    public DirectExchange xExchange(){
        return new DirectExchange(X_EXCHANGE);
    }

    //声明yExchange 别名
    @Bean("yExchange")
    public DirectExchange yExchange(){
        return new DirectExchange(Y_DEAD_LETTER_EXCHANGE);
    }

    //声明普通队列 ttl为10s
    @Bean("queueA")
    public Queue queueA(){
        Map<String, Object> arguments = new HashMap<>(3);
        //设置死信交换机
        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        //设置死信RoutingKey
        arguments.put("x-dead-letter-routing-key","YD");
        //设置TTL  单位ms
        arguments.put("x-message-ttl",10000);
        return QueueBuilder.durable(QUEUE_A).withArguments(arguments).build();
    }

    //声明普通队列 ttl为40s
    @Bean("queueB")
    public Queue queueB(){
        Map<String, Object> arguments = new HashMap<>(3);
        //设置死信交换机
        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        //设置死信RoutingKey
        arguments.put("x-dead-letter-routing-key","YD");
        //设置TTL  单位ms
        arguments.put("x-message-ttl",40000);
        return QueueBuilder.durable(QUEUE_B).withArguments(arguments).build();
    }

    @Bean("queueC")
    public Queue queueC(){
        Map<String, Object> arguments = new HashMap<>(3);
        //设置死信交换机
        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
        //设置死信RoutingKey
        arguments.put("x-dead-letter-routing-key","YD");
        //设置TTL  单位ms
        return QueueBuilder.durable(QUEUE_C).withArguments(arguments).build();
    }

    //死信队列
    @Bean("queueD")
    public Queue queueD(){
        return QueueBuilder.durable(DEAD_LETTER_QUEUE).build();
    }

    //绑定
    @Bean
    public Binding queueAbindindX(@Qualifier("queueA") Queue queueA,@Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueA).to(xExchange).with("XA");
    }

    //绑定
    @Bean
    public Binding queueBbindindX(@Qualifier("queueB") Queue queueB,@Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueB).to(xExchange).with("XB");
    }

    //绑定
    @Bean
    public Binding queueCbindindX(@Qualifier("queueC") Queue queueB,@Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueB).to(xExchange).with("XC");
    }

    //绑定
    @Bean
    public Binding queueDbindindY(@Qualifier("queueD") Queue queueD,@Qualifier("yExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueD).to(xExchange).with("YD");
    }

}
