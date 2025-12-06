package com.palavras.etiquetas.relationshipservice.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "relacionamentos")
public class Relacionamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_palavra", nullable = false)
    private Long idPalavra;
    
    @Column(name = "id_etiqueta", nullable = false)
    private Long idEtiqueta;
    
    public Relacionamento() {
    }
    
    public Relacionamento(Long idPalavra, Long idEtiqueta) {
        this.idPalavra = idPalavra;
        this.idEtiqueta = idEtiqueta;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getIdPalavra() {
        return idPalavra;
    }
    
    public void setIdPalavra(Long idPalavra) {
        this.idPalavra = idPalavra;
    }
    
    public Long getIdEtiqueta() {
        return idEtiqueta;
    }
    
    public void setIdEtiqueta(Long idEtiqueta) {
        this.idEtiqueta = idEtiqueta;
    }
}
