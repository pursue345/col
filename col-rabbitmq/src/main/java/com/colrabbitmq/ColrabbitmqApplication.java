package com.colrabbitmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ColrabbitmqApplication {

    public static void main(String[] args) {
        SpringApplication.run(ColrabbitmqApplication.class, args);
    }

}

/**
 * 死信队列：保证订单业务的消息数据不丢失
 * 当消息发生异常，将消息投入死信队列；用户商城下单成功，指定时间未支付时自动失效
 */
