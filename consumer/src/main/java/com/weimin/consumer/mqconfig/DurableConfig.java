package com.weimin.consumer.mqconfig;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 持久化
@Configuration
public class DurableConfig {

    @Bean
    public DirectExchange durableExchange(){
        // 1. 交换机名字
        // 2. 是否持久化
        // 3. 是否自动删除
        return new DirectExchange("durable.exchange", true, false);
    }

    @Bean
    public Queue durableQueue(){
        return QueueBuilder.durable("durable.queue").build();
    }
}
