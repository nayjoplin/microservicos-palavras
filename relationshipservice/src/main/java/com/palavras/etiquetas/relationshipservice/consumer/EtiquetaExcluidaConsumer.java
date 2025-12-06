package com.palavras.etiquetas.relationshipservice.consumer;

import com.palavras.etiquetas.relationshipservice.config.RabbitConfig;
import com.palavras.etiquetas.relationshipservice.repository.RelacionamentoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EtiquetaExcluidaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(EtiquetaExcluidaConsumer.class);
    private final RelacionamentoRepository relacionamentoRepository;

    public EtiquetaExcluidaConsumer(RelacionamentoRepository relacionamentoRepository) {
        this.relacionamentoRepository = relacionamentoRepository;
    }

    @RabbitListener(queues = RabbitConfig.ETIQUETAS_EXCLUIDAS_QUEUE)
    @Transactional
    public void handleEtiquetaExcluida(Long idEtiqueta) {
        logger.info("Removing relationships for deleted etiqueta: {}", idEtiqueta);
        relacionamentoRepository.deleteByIdEtiqueta(idEtiqueta);
    }
}
