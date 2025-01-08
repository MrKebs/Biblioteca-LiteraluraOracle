package com.alura.literaluraOracle.model;

import jakarta.persistence.*;

@Entity
@Table(name = "libros")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private Double descargas;
    private String idioma;
    @ManyToOne
    private Autor autor;

    public Libro() {
    }

    public Libro(String titulo, Autor autor, String idioma, Double descargas) {
        this.titulo = titulo;
        this.descargas = descargas;
        this.idioma = idioma;
        this.autor = autor;
    }

    public Libro(DatosLibro datosLibro) {
        this.titulo = datosLibro.titulo();
        this.descargas = datosLibro.descargas();
        this.idioma = datosLibro.idioma().isEmpty() ? "N/A" : datosLibro.idioma().get(0);
        this.autor = new Autor(datosLibro.autores().get(0));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Double getDescargas() {
        return descargas;
    }

    public void setDescargas(Double descargas) {
        this.descargas = descargas;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return titulo +
               " (" + autor.getNombre() + ")" +
               " [" + idioma + "]" +
               " | Descargas: " + descargas;
    }
}
