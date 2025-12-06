package com.palavras.etiquetas.relationshipservice.controller;

import com.palavras.etiquetas.relationshipservice.client.AuthClient;
import com.palavras.etiquetas.relationshipservice.client.DomainClient;
import com.palavras.etiquetas.relationshipservice.dto.CreateRelacionamentoRequest;
import com.palavras.etiquetas.relationshipservice.dto.ValidateResponse;
import com.palavras.etiquetas.relationshipservice.entity.Relacionamento;
import com.palavras.etiquetas.relationshipservice.repository.RelacionamentoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/relacionamentos")
public class RelacionamentoController {

    private final RelacionamentoRepository relacionamentoRepository;
    private final AuthClient authClient;
    private final DomainClient domainClient;

    public RelacionamentoController(RelacionamentoRepository relacionamentoRepository,
                                   AuthClient authClient, DomainClient domainClient) {
        this.relacionamentoRepository = relacionamentoRepository;
        this.authClient = authClient;
        this.domainClient = domainClient;
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

    @GetMapping("/palavras/{idPalavra}")
    public ResponseEntity<?> getEtiquetasByPalavra(@PathVariable Long idPalavra,
                                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        List<Long> etiquetas = relacionamentoRepository.findByIdPalavra(idPalavra)
                .stream()
                .map(Relacionamento::getIdEtiqueta)
                .toList();
        return ResponseEntity.ok(etiquetas);
    }

    @GetMapping("/etiquetas/{idEtiqueta}")
    public ResponseEntity<?> getPalavrasByEtiqueta(@PathVariable Long idEtiqueta,
                                                   @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        List<Long> palavras = relacionamentoRepository.findByIdEtiqueta(idEtiqueta)
                .stream()
                .map(Relacionamento::getIdPalavra)
                .toList();
        return ResponseEntity.ok(palavras);
    }

    @PostMapping
    public ResponseEntity<?> createRelacionamento(@RequestBody CreateRelacionamentoRequest request,
                                                  @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String token = extractToken(authHeader);
        
        if (!domainClient.palavraExists(request.getIdPalavra(), token)) {
            return ResponseEntity.badRequest().body("Palavra not found");
        }
        
        if (!domainClient.etiquetaExists(request.getIdEtiqueta(), token)) {
            return ResponseEntity.badRequest().body("Etiqueta not found");
        }

        Relacionamento relacionamento = new Relacionamento(request.getIdPalavra(), request.getIdEtiqueta());
        Relacionamento saved = relacionamentoRepository.save(relacionamento);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRelacionamento(@PathVariable Long id,
                                                  @RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        if (!relacionamentoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        relacionamentoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<?> listAll(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (!isAuthenticated(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        return ResponseEntity.ok(relacionamentoRepository.findAll());
    }
}
