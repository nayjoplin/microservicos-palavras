package com.palavras.etiquetas.relationshipservice.consumer;

import com.palavras.etiquetas.relationshipservice.config.RabbitConfig;
import com.palavras.etiquetas.relationshipservice.repository.RelacionamentoRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PalavraExcluidaConsumer {

    private final RelacionamentoRepository relacionamentoRepository;

    public PalavraExcluidaConsumer(RelacionamentoRepository relacionamentoRepository) {
        this.relacionamentoRepository = relacionamentoRepository;
    }

    @RabbitListener(queues = RabbitConfig.PALAVRAS_EXCLUIDAS_QUEUE)
    @Transactional
    public void handlePalavraExcluida(Long idPalavra) {
        System.out.println("Removing relationships for deleted palavra: " + idPalavra);
        relacionamentoRepository.deleteByIdPalavra(idPalavra);
    }
}
