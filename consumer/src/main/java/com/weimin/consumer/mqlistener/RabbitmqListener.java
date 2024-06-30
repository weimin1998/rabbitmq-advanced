package com.weimin.consumer.mqlistener;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitmqListener {

    @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue(String message) {
        log.info("收到消息: " + message);
    }
}
