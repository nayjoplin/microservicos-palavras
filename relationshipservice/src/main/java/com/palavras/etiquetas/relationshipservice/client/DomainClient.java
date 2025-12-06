package com.palavras.etiquetas.relationshipservice.client;

import com.palavras.etiquetas.relationshipservice.dto.ExistsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class DomainClient {

    private final WebClient wordWebClient;
    private final WebClient tagWebClient;

    public DomainClient(@Value("${word.service.url}") String wordServiceUrl,
                       @Value("${tag.service.url}") String tagServiceUrl) {
        this.wordWebClient = WebClient.builder()
                .baseUrl(wordServiceUrl)
                .build();
        this.tagWebClient = WebClient.builder()
                .baseUrl(tagServiceUrl)
                .build();
    }

    public boolean palavraExists(Long idPalavra, String token) {
        try {
            ExistsResponse response = wordWebClient.get()
                    .uri("/palavras/existe/" + idPalavra)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(ExistsResponse.class)
                    .block();
            return response != null && response.isExists();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean etiquetaExists(Long idEtiqueta, String token) {
        try {
            ExistsResponse response = tagWebClient.get()
                    .uri("/etiquetas/existe/" + idEtiqueta)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(ExistsResponse.class)
                    .block();
            return response != null && response.isExists();
        } catch (Exception e) {
            return false;
        }
    }
}
