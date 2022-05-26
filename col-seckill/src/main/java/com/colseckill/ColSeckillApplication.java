package com.colseckill;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableRabbit
@SpringBootApplication
@MapperScan(basePackages = "com.colseckill.mapper")
@EnableAsync
public class ColSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(ColSeckillApplication.class, args);
    }

}
