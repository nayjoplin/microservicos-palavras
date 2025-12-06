package com.palavras.etiquetas.labelservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "etiquetas")
public class Etiqueta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    public Etiqueta() {
    }
    
    public Etiqueta(String nome) {
        this.nome = nome;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
}
