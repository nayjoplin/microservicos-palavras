package com.palavras.etiquetas.relationshipservice.dto;

public class CreateRelacionamentoRequest {
    private Long idPalavra;
    private Long idEtiqueta;

    public CreateRelacionamentoRequest() {
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
