package com.palavras.etiquetas.wordservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String PALAVRAS_EXCLUIDAS_QUEUE = "palavras.excluidas";

    @Bean
    public Queue palavrasExcluidasQueue() {
        return new Queue(PALAVRAS_EXCLUIDAS_QUEUE, true);
    }
}
