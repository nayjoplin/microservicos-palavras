package com.palavras.etiquetas.relationshipservice.repository;

import com.palavras.etiquetas.relationshipservice.entity.Relacionamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelacionamentoRepository extends JpaRepository<Relacionamento, Long> {
    List<Relacionamento> findByIdPalavra(Long idPalavra);
    List<Relacionamento> findByIdEtiqueta(Long idEtiqueta);
    void deleteByIdPalavra(Long idPalavra);
    void deleteByIdEtiqueta(Long idEtiqueta);
}
