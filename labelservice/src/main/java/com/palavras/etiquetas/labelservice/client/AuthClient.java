package com.palavras.etiquetas.labelservice.client;

import com.palavras.etiquetas.labelservice.dto.ValidateResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class AuthClient {

    private final WebClient webClient;

    public AuthClient(@Value("${auth.service.url}") String authServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(authServiceUrl)
                .build();
    }

    public ValidateResponse validateToken(String token) {
        try {
            return webClient.get()
                    .uri("/auth/validate")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(ValidateResponse.class)
                    .block();
        } catch (Exception e) {
            ValidateResponse response = new ValidateResponse();
            response.setValid(false);
            return response;
        }
    }
}
