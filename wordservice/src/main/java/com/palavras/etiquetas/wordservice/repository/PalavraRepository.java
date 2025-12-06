package com.palavras.etiquetas.wordservice.repository;

import com.palavras.etiquetas.wordservice.entity.Palavra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PalavraRepository extends JpaRepository<Palavra, Long> {
}
