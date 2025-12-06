package com.palavras.etiquetas.labelservice.repository;

import com.palavras.etiquetas.labelservice.entity.Etiqueta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {
}
