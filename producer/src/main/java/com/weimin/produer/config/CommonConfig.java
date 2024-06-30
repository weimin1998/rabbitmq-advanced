package com.weimin.produer.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

@Slf4j
@Configuration
public class CommonConfig implements ApplicationContextAware {


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        RabbitTemplate rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {

            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                Message message = returnedMessage.getMessage();
                int replyCode = returnedMessage.getReplyCode();
                String replyText = returnedMessage.getReplyText();
                String exchange = returnedMessage.getExchange();
                String routingKey = returnedMessage.getRoutingKey();

                Integer receivedDelay = message.getMessageProperties().getReceivedDelay();
                if (!ObjectUtils.isEmpty(receivedDelay) && receivedDelay > 0) {
                    log.info("消息发送到队列成功！");
                    return;
                }
                log.error("消息发送到队列失败，响应码：{}，失败原因：{}，交换机：{}，路由key：{}，消息：{}。", replyCode, replyText, exchange, routingKey, message);

                // 如果有需要，可以重发消息
                log.info("retry");
            }
        });
    }
}
