package com.alura.literaluraOracle.model;

import jakarta.persistence.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "autores")
public class Autor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Year anioNacimiento;
    private Year anioFallecimiento;
    @OneToMany(mappedBy = "autor", fetch = FetchType.EAGER)
    private List<Libro> libros = new ArrayList<>();

    public Autor() {
    }

    public Autor(String nombre, Year anioNacimiento, Year anioFallecimiento) {
        this.nombre = nombre;
        this.anioNacimiento = anioNacimiento;
        this.anioFallecimiento = anioFallecimiento;
    }

    public Autor(DatosAutor datosAutor) {
        this.nombre = datosAutor.nombre();
        this.anioNacimiento = datosAutor.anioNacimiento() != null ? Year.of(datosAutor.anioNacimiento()) : null;
        this.anioFallecimiento = datosAutor.anioFallecimiento() != null ? Year.of(datosAutor.anioFallecimiento()) : null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Year getAnioNacimiento() {
        return anioNacimiento;
    }

    public void setAnioNacimiento(Year anioNacimiento) {
        this.anioNacimiento = anioNacimiento;
    }

    public Year getAnioFallecimiento() {
        return anioFallecimiento;
    }

    public void setAnioFallecimiento(Year anioFallecimiento) {
        this.anioFallecimiento = anioFallecimiento;
    }

    public List<Libro> getLibros() {
        return libros;
    }

    public void setLibros(List<Libro> libros) {
        this.libros = libros;
    }

    @Override
    public String toString() {
        String anioNacimiento = this.anioNacimiento != null ? this.anioNacimiento.toString() : "desconocido";
        String anioFallecimiento = this.anioFallecimiento != null ? this.anioFallecimiento.toString() : "desconocido";
        return nombre + " (" + anioNacimiento + "-" + anioFallecimiento + ")";
    }
}
