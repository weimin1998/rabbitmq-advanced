package com.weimin.produer.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/producer")
public class ProducerController {

    @Resource
    RabbitTemplate rabbitTemplate;


    // http://localhost:8080/producer/sendMsg
    // http://localhost:8080/producer/sendMsg?exchange=amq.topic1
    // http://localhost:8080/producer/sendMsg?routingKey=simple1.test
    // 默认发送到amq.topic 交换机，这个是官方自带的交换机
    // 如果想演示失败的情况，可以指定一个不存在的交换机，也可以指定错误的routekey
    @GetMapping("/sendMsg")
    public String simple(@RequestParam(required = false, defaultValue = "amq.topic") String exchange, @RequestParam(required = false, defaultValue = "simple.test") String routingKey) {
        String message = "hello, spring amqp!";

        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        correlationData.getFuture().addCallback(new SuccessCallback<CorrelationData.Confirm>() {
            @Override
            public void onSuccess(CorrelationData.Confirm confirm) {
                if (confirm.isAck()) {
                    log.info("消息成功投递到交换机！消息id：{}", correlationData.getId());
                } else {
                    // nack
                    log.error("消息未投递到交换机！，消息id：{}", correlationData.getId());
                }
            }
        }, new FailureCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("消息发送失败！", throwable);
            }
        });
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);

        return "发送成功";
    }


    // 发送持久化消息
    // http://localhost:8080/producer/sendDurableMsg
    @GetMapping("/sendDurableMsg")
    public String sendDurableMsg() {
        Message message = MessageBuilder.
                withBody("hello, my name is wm".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)// 投递方式，为持久的
                .build();

        rabbitTemplate.convertAndSend("durable.queue", message);
        // 发送完消息后，由于消费者没有配置监听器，所以消息不会被消费。
        // 这时，重启mq，消息还存在。
        return "发送成功";
    }
}
