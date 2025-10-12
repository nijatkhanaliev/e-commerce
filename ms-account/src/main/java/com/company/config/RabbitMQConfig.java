package com.company.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ACCOUNT_QUEUE = "account-queue";
    public static final String ACCOUNT_EXCHANGE = "account-exchange";
    public static final String ACCOUNT_ROUTING_KEY = "account.*";


    @Bean
    public Queue accountQueue() {
        return QueueBuilder.durable(ACCOUNT_QUEUE)
                .withArgument("x-dead-letter-exchange", ACCOUNT_EXCHANGE + ".dlx")
                .withArgument("x-dead-letter-routing-key", ACCOUNT_ROUTING_KEY + ".dlq")
                .build();
    }

    @Bean
    public Queue accountDLQ() {
        return QueueBuilder.durable(ACCOUNT_QUEUE + ".dlq").build();
    }

    @Bean
    public TopicExchange deadLetterExchange() {
        return new TopicExchange(ACCOUNT_EXCHANGE + ".dlx");
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(ACCOUNT_EXCHANGE);
    }

    @Bean
    public Binding bindingAccount(Queue accountQueue, TopicExchange topicExchange) {
        return BindingBuilder.bind(accountQueue)
                .to(topicExchange)
                .with(ACCOUNT_ROUTING_KEY);
    }

    @Bean
    public Binding bindingEmailDLQ(Queue accountDLQ, TopicExchange deadLetterExchange) {
        return BindingBuilder.bind(accountDLQ)
                .to(deadLetterExchange)
                .with(ACCOUNT_ROUTING_KEY + ".dlq");
    }


    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());

        return rabbitTemplate;
    }

}
