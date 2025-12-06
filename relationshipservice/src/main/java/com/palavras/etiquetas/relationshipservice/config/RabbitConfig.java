package com.palavras.etiquetas.relationshipservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String PALAVRAS_EXCLUIDAS_QUEUE = "palavras.excluidas";
    public static final String ETIQUETAS_EXCLUIDAS_QUEUE = "etiquetas.excluidas";

    @Bean
    public Queue palavrasExcluidasQueue() {
        return new Queue(PALAVRAS_EXCLUIDAS_QUEUE, true);
    }

    @Bean
    public Queue etiquetasExcluidasQueue() {
        return new Queue(ETIQUETAS_EXCLUIDAS_QUEUE, true);
    }
}
