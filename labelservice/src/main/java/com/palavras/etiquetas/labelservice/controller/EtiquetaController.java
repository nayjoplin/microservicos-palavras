package com.palavras.etiquetas.labelservice.controller;

import com.palavras.etiquetas.labelservice.client.AuthClient;
import com.palavras.etiquetas.labelservice.client.RelationshipClient;
import com.palavras.etiquetas.labelservice.config.RabbitConfig;
import com.palavras.etiquetas.labelservice.dto.ValidateResponse;
import com.palavras.etiquetas.labelservice.entity.Etiqueta;
import com.palavras.etiquetas.labelservice.repository.EtiquetaRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/etiquetas")
public class EtiquetaController {

    private final EtiquetaRepository etiquetaRepository;
    private final AuthClient authClient;
    private final RelationshipClient relationshipClient;
    private final RabbitTemplate rabbitTemplate;

    public EtiquetaController(EtiquetaRepository etiquetaRepository, AuthClient authClient,
                             RelationshipClient relationshipClient, RabbitTemplate rabbitTemplate) {
        this.etiquetaRepository = etiquetaRepository;
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
        return ResponseEntity.ok(etiquetaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id,
                                     @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return etiquetaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/existe/{id}")
    public ResponseEntity<?> existe(@PathVariable Long id,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        boolean exists = etiquetaRepository.existsById(id);
        return ResponseEntity.ok(Map.of("exists", exists));
    }

    @GetMapping("/{id}/palavras")
    public ResponseEntity<?> getPalavras(@PathVariable Long id,
                                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (!etiquetaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        String token = extractToken(authHeader);
        List<Long> palavras = relationshipClient.getPalavrasForEtiqueta(id, token);
        return ResponseEntity.ok(palavras);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Etiqueta etiqueta,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        Etiqueta saved = etiquetaRepository.save(etiqueta);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Etiqueta etiqueta,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return etiquetaRepository.findById(id)
                .map(existing -> {
                    existing.setNome(etiqueta.getNome());
                    return ResponseEntity.ok(etiquetaRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id,
                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (!etiquetaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        etiquetaRepository.deleteById(id);
        rabbitTemplate.convertAndSend(RabbitConfig.ETIQUETAS_EXCLUIDAS_QUEUE, id);
        return ResponseEntity.noContent().build();
    }
}
