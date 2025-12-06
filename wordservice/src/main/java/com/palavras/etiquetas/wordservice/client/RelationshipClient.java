package com.palavras.etiquetas.wordservice.client;

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

    public List<Long> getEtiquetasForPalavra(Long idPalavra, String token) {
        try {
            return webClient.get()
                    .uri("/relacionamentos/palavras/" + idPalavra)
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
