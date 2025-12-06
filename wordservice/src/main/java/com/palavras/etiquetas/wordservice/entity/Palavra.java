package com.palavras.etiquetas.wordservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "palavras")
public class Palavra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String texto;
    
    public Palavra() {
    }
    
    public Palavra(String texto) {
        this.texto = texto;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTexto() {
        return texto;
    }
    
    public void setTexto(String texto) {
        this.texto = texto;
    }
}
