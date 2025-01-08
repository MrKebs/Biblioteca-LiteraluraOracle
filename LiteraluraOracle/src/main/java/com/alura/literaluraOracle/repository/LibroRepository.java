package com.alura.literaluraOracle.repository;

import com.alura.literaluraOracle.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LibroRepository extends JpaRepository<Libro, Long> {

    public Optional<Libro> findByTitulo(String titulo);

    List<Libro> findByIdioma(String idioma);
}
