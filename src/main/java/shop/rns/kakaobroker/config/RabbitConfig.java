package shop.rns.kakaobroker.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import javax.naming.directory.DirContext;

import static shop.rns.kakaobroker.utils.rabbitmq.RabbitUtil.*;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // Exchange
    @Bean
    public DirectExchange kakaoWorkExchange() {
        return new DirectExchange(KAKAO_EXCHANGE_NAME);
    }

    @Bean
    public DirectExchange kakaoReceiveExchange(){ return new DirectExchange(RECEIVE_EXCHANGE_NAME); }

    @Bean
    public DirectExchange dlxKakaoExchange(){ return new DirectExchange(DLX_EXCHANGE_NAME); }

    @Bean
    public DirectExchange DeadKakaoExchange(){ return new DirectExchange(DEAD_EXCHANGE_NAME); }

    // Queue
    @Bean
    public Queue kakaoWorkKEQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", KE_WAIT_ROUTING_KEY);
        return new Queue(KE_WORK_QUEUE_NAME, true, false, false, args);
    }

    @Bean
    public Queue kakaoReceiveKEQueue() {
        return new Queue(KE_RECEIVE_QUEUE_NAME, true);
    }

    @Bean
    public Queue kakaoWaitKEQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", WAIT_TTL);
        args.put("x-dead-letter-exchange", KAKAO_EXCHANGE_NAME);
        args.put("x-dead-letter-routing-key", KE_WORK_ROUTING_KEY);

        return new Queue(KE_WAIT_QUEUE_NAME, true, false,false, args);
    }

    // Binding
    // work queue + work exchange
    @Bean
    public Binding bindingKakaoKE(DirectExchange kakaoWorkExchange, Queue kakaoWorkKEQueue){
        return BindingBuilder.bind(kakaoWorkKEQueue)
                .to(kakaoWorkExchange)
                .with(KE_WORK_ROUTING_KEY);
    }


    @Bean
    public Binding bindingKakaoReceiveKE(DirectExchange kakaoReceiveExchange, Queue kakaoReceiveKEQueue){
        return BindingBuilder.bind(kakaoReceiveKEQueue)
                .to(kakaoReceiveExchange)
                .with(KE_RECEIVE_ROUTING_KEY);
    }

    @Bean
    public Binding bindingKakaoDlxKE(DirectExchange kakaoDlxExchange, Queue kakaoWaitKEQueue){
        return BindingBuilder.bind(kakaoWaitKEQueue)
                .to(kakaoDlxExchange)
                .with(KE_WAIT_ROUTING_KEY);
    }
}
