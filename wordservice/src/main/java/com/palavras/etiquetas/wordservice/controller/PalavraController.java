package com.palavras.etiquetas.wordservice.controller;

import com.palavras.etiquetas.wordservice.client.AuthClient;
import com.palavras.etiquetas.wordservice.client.RelationshipClient;
import com.palavras.etiquetas.wordservice.config.RabbitConfig;
import com.palavras.etiquetas.wordservice.dto.ValidateResponse;
import com.palavras.etiquetas.wordservice.entity.Palavra;
import com.palavras.etiquetas.wordservice.repository.PalavraRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/palavras")
public class PalavraController {

    private final PalavraRepository palavraRepository;
    private final AuthClient authClient;
    private final RelationshipClient relationshipClient;
    private final RabbitTemplate rabbitTemplate;

    public PalavraController(PalavraRepository palavraRepository, AuthClient authClient,
                            RelationshipClient relationshipClient, RabbitTemplate rabbitTemplate) {
        this.palavraRepository = palavraRepository;
        this.authClient = authClient;
        this.relationshipClient = relationshipClient;
        this.rabbitTemplate = rabbitTemplate;
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean isAuthenticated(String authHeader) {
        String token = extractToken(authHeader);
        if (token == null) {
            return false;
        }
        ValidateResponse response = authClient.validateToken(token);
        return response != null && response.isValid();
    }

    @GetMapping
    public ResponseEntity<?> listAll(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return ResponseEntity.ok(palavraRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id,
                                     @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return palavraRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/existe/{id}")
    public ResponseEntity<?> existe(@PathVariable Long id,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        boolean exists = palavraRepository.existsById(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/{id}/etiquetas")
    public ResponseEntity<?> getEtiquetas(@PathVariable Long id,
                                         @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (!palavraRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        String token = extractToken(authHeader);
        List<Long> etiquetas = relationshipClient.getEtiquetasForPalavra(id, token);
        return ResponseEntity.ok(etiquetas);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Palavra palavra,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        Palavra saved = palavraRepository.save(palavra);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Palavra palavra,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return palavraRepository.findById(id)
                .map(existing -> {
                    existing.setTexto(palavra.getTexto());
                    return ResponseEntity.ok(palavraRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (!palavraRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        palavraRepository.deleteById(id);
        rabbitTemplate.convertAndSend(RabbitConfig.PALAVRAS_EXCLUIDAS_QUEUE, id);
        return ResponseEntity.noContent().build();
    }
}
