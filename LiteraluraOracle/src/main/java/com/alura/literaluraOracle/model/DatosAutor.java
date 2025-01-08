package com.alura.literaluraOracle.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public record DatosAutor(@JsonAlias("name") String nombre,
                         @JsonAlias("birth_year") Integer anioNacimiento,
                         @JsonAlias("death_year") Integer anioFallecimiento) {
}