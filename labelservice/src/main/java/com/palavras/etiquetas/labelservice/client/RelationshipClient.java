package com.palavras.etiquetas.labelservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class RelationshipClient {

    private final WebClient webClient;

    public RelationshipClient(@Value("${relationship.service.url}") String relationshipServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(relationshipServiceUrl)
                .build();
    }

    public List<Long> getPalavrasForEtiqueta(Long idEtiqueta, String token) {
        try {
            return webClient.get()
                    .uri("/relacionamentos/etiquetas/" + idEtiqueta)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToFlux(Long.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            return List.of();
        }
    }
}
