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

    public static final String STOCK_UPDATED_QUEUE = "stock-updated-queue";
    public static final String ORDER_CREATED_EXCHANGE = "order-exchange";
    public static final String STOCK_UPDATED_ROUTING_KEY = "stock.updated";
    public static final String PAYMENT_SUCCESS_ROUTING_KEY = "payment.success";
    public static final String PAYMENT_FAILED_ROUTING_KEY = "payment.failed";

    @Bean
    public Queue stockUpdatedQueue() {
        return QueueBuilder.durable(STOCK_UPDATED_QUEUE)
                .withArgument("x-dead-letter-exchange", ORDER_CREATED_EXCHANGE + ".dlx")
                .withArgument("x-dead-letter-routing-key", STOCK_UPDATED_ROUTING_KEY + ".dlq")
                .build();
    }

    @Bean
    public Queue stockUpdatedQueueDLQ() {
        return QueueBuilder.durable(STOCK_UPDATED_QUEUE + ".dlq")
                .build();
    }

    @Bean
    public TopicExchange stockUpdatedExchange(){
        return new TopicExchange(ORDER_CREATED_EXCHANGE);
    }

    @Bean
    public TopicExchange deadLetterExchange(){
        return new TopicExchange(ORDER_CREATED_EXCHANGE + ".dlx");
    }

    @Bean
    public Binding bindingStockUpdated(Queue stockUpdatedQueue, TopicExchange stockUpdatedExchange){
        return BindingBuilder.bind(stockUpdatedQueue)
                .to(stockUpdatedExchange)
                .with(STOCK_UPDATED_ROUTING_KEY);
    }

    @Bean
    public Binding bindingStockUpdatedDLQ(Queue stockUpdatedQueueDLQ, TopicExchange deadLetterExchange){
        return BindingBuilder.bind(stockUpdatedQueueDLQ)
                .to(deadLetterExchange)
                .with(STOCK_UPDATED_ROUTING_KEY + ".dlq");
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
