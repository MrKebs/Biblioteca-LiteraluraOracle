package com.alura.literaluraOracle.main;

import com.alura.literaluraOracle.model.Autor;
import com.alura.literaluraOracle.model.Datos;
import com.alura.literaluraOracle.model.DatosLibro;
import com.alura.literaluraOracle.model.Libro;
import com.alura.literaluraOracle.repository.AutorRepository;
import com.alura.literaluraOracle.repository.LibroRepository;
import com.alura.literaluraOracle.service.ConsumoAPI;
import com.alura.literaluraOracle.service.ConvierteDatos;

import java.time.Year;
import java.util.*;

public class Principal {

    private static final String URL_BASE = "https://gutendex.com/books/";
    private static final String URL_BUSQUEDA = "?search=";
    private static final int LIMITE_LIBROS = 20; // Límite de libros a mostrar
    private static final int LIMITE_MAXIMO = 40; // Límite de libros para volver al menú

    private final Scanner input;
    private final ConsumoAPI consumoAPI;
    private final ConvierteDatos convierteDatos;
    private final LibroRepository libroRepository;
    private final AutorRepository autorRepository;

    public Principal(LibroRepository libroRepository, AutorRepository autorRepository) {
        this.input = new Scanner(System.in);
        this.consumoAPI = new ConsumoAPI();
        this.convierteDatos = new ConvierteDatos();
        this.libroRepository = libroRepository;
        this.autorRepository = autorRepository;
    }

    public void menu() {
        boolean salir = false;

        while (!salir) {
            System.out.print("""
                    \n================================================
                      ¡Bienvenidos a la Biblioteca LiteraluraOracle!
                    ==================================================
                    1. Buscar Libros por Título
                    2. Listar Libros Registrados
                    3. Listar Autores Registrados
                    4. Listar Autores Vivos por Año
                    5. Listar Autores Nacidos por Año
                    6. Listar Autores por Año Fallecimiento
                    7. Listar libros en Otros Idiomas
                    0. Salir
                    ==================================================
                    Por favor, selecciona una opción mostrada en pantalla:\s""");

            var opcion = obtenerOpcion();

            switch (opcion) {
                case 1 -> buscarLibrosPorTitulo();
                case 2 -> listarLibros();
                case 3 -> listarAutores();
                case 4 -> listarAutoresVivosEnUnDeterminadoAnio();
                case 5 -> listarAutoresNacidosEnUnDeterminadoAnio();
                case 6 -> listarAutoresPorAnioDeSuMuerte();
                case 7 -> listarLibrosEnUnDeterminadoIdioma();
                case 0 -> {
                    System.out.println("\n¡Gracias por usar LiteraluraOracle! ¡Nos vemos pronto!");
                    salir = true;
                }
                default -> System.out.println("Opción no válida. Por favor, selecciona un número del 0 al 7.");
            }
        }
        input.close();
    }

    private Integer obtenerOpcion() {
        while (true) {
            try {
                return Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Por favor, ingresa una opción válida: ");
            }
        }
    }

    private void buscarLibrosPorTitulo() {
        System.out.println("\nPor favor, ingresa el título del libro que deseas buscar.");
        System.out.print("Título: ");
        var nombreLibro = obtenerNombre();

        Libro libro = buscarLibro(nombreLibro);

        if (libro != null) {
            // Buscar el libro en la base de datos
            Optional<Libro> libroOptional = libroRepository.findByTitulo(libro.getTitulo());

            if (libroOptional.isEmpty()) {
                System.out.println("\nLibro encontrado:");
                System.out.println("==============================");
                System.out.println(libro);
                System.out.println("==============================");

                // Verificar y asignar el autor
                Autor autor = libro.getAutor();
                if (autor != null) {
                    Optional<Autor> autorOptional = autorRepository.findByNombre(autor.getNombre());

                    if (autorOptional.isEmpty()) {
                        // Guardar el autor si no existe en la base de datos
                        try {
                            autor = autorRepository.save(autor);
                            System.out.println("El autor ha sido guardado correctamente en la base de datos.");
                        } catch (Exception e) {
                            System.out.println("Error al guardar el autor: " + e.getMessage());
                            return; // Salir si no se puede guardar el autor
                        }
                    } else {
                        // Usar el autor existente
                        autor = autorOptional.get();
                    }

                    // Asignar el autor al libro
                    libro.setAutor(autor);
                }

                try {
                    // Guardar el libro
                    libroRepository.save(libro);
                    System.out.println("El libro ha sido guardado correctamente en la base de datos.");
                } catch (Exception e) {
                    System.out.println("Error al guardar el libro: " + e.getMessage());
                }
            } else {
                System.out.println("El libro ya está registrado en la base de datos.");
            }
        }
    }

    private String obtenerNombre() {
        return input.nextLine().trim();
    }

    private Libro buscarLibro(String nombreLibro) {
        String json = consumoAPI.obtenerDatos(URL_BASE + URL_BUSQUEDA + nombreLibro.replace(" ", "+").toLowerCase());
        Datos datos = convierteDatos.obtenerDatos(json, Datos.class);

        if (datos.libros().isEmpty()) {
            System.out.println("\nNo se encontraron libros que coincidan con \"" + nombreLibro + "\".");
            return null;
        }

        if (datos.libros().size() > LIMITE_MAXIMO) {
            System.out.println("\nSe encontraron muchos libros. Por favor, refina tu búsqueda para obtener mejores resultados.");
            return null;
        }

        List<Libro> librosLimitados = datos.libros()
                .stream()
                .sorted(Comparator.comparing(DatosLibro::descargas).reversed())
                .limit(LIMITE_LIBROS)
                .map(Libro::new)
                .toList();

        System.out.println("\nResultados encontrados:");
        System.out.println("==============================");
        for (int i = 0; i < librosLimitados.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, librosLimitados.get(i));
        }

        // Si hay más libros que el límite, mostrar un mensaje
        if (datos.libros().size() > LIMITE_LIBROS) {
            System.out.println("\nSe mostraron los primeros " + LIMITE_LIBROS + " libros. Para ver más resultados, refine su búsqueda.");
        }

        // Permitir que el usuario elija un libro
        System.out.print("\nPor favor, selecciona el número del libro que deseas guardar (1-" + librosLimitados.size() + "): ");
        int opcion = obtenerOpcion();

        // Validar la opción del usuario
        while (opcion < 1 || opcion > librosLimitados.size()) {
            System.out.print("Opción no válida. Por favor, selecciona un número entre 1 y " + librosLimitados.size() + ": ");
            opcion = obtenerOpcion();
        }

        // Devolver el libro seleccionado
        return librosLimitados.get(opcion - 1);
    }

    private void listarLibros() {
        List<Libro> libros = libroRepository.findAll();

        if (libros.isEmpty()) {
            System.out.println("\nNo hay libros registrados en la base de datos.");
        } else {
            System.out.println("\nLibros registrados:");
            System.out.println("==============================");
            libros.stream()
                    .sorted(Comparator.comparing(Libro::getDescargas).reversed())
                    .forEach(System.out::println);
        }
    }

    private void listarAutores() {
        List<Autor> autores = autorRepository.findAll();

        if (autores.isEmpty()) {
            System.out.println("\nNo hay autores registrados en la base de datos.");
        } else {
            System.out.println("\nAutores registrados:");
            System.out.println("==============================");
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(System.out::println);
        }
    }

    private void listarAutoresVivosEnUnDeterminadoAnio() {
        System.out.print("\nPor favor, ingresa el año de ejercicio de los autores: ");
        var anio = obtenerAnio();

        List<Autor> autores = autorRepository.buscarAutoresVivosPorAnio(anio);

        if (autores.isEmpty()) {
            System.out.println("\nNo se encontraron autores en ejercicio en el " + anio + ".");
        } else {
            System.out.println("\nAutores en ejercicio en el " + anio + ":");
            System.out.println("==============================");
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(System.out::println);
        }
    }

    private Year obtenerAnio() {
        while (true) {
            try {
                int anio = Integer.parseInt(input.nextLine().trim());
                return Year.of(anio);
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Por favor, ingresa un año válido: ");
            }
        }
    }

    private void listarAutoresNacidosEnUnDeterminadoAnio() {
        System.out.print("\nPor favor, ingresa el año de nacimiento de los autores: ");
        var anio = obtenerAnio();

        List<Autor> autores = autorRepository.findByAnioNacimiento(anio);

        if (autores.isEmpty()) {
            System.out.println("\nNo se encontraron autores nacidos en " + anio + ".");
        } else {
            System.out.println("\nAutores nacidos en " + anio + ":");
            System.out.println("==============================");
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(System.out::println);
        }
    }

    private void listarAutoresPorAnioDeSuMuerte() {
        System.out.print("\nPor favor, ingrese el año de fallecimiento de los autores: ");
        var anio = obtenerAnio();

        List<Autor> autores = autorRepository.findByAnioFallecimiento(anio);

        if (autores.isEmpty()) {
            System.out.println("\nNo se encontraron autores fallecidos en el " + anio + ".");
        } else {
            System.out.println("\nAutores fallecidos en el " + anio + ":");
            System.out.println("==============================");
            autores.stream()
                    .sorted(Comparator.comparing(Autor::getNombre))
                    .forEach(System.out::println);
        }
    }

    private void listarLibrosEnUnDeterminadoIdioma() {
        System.out.print("""
                \nIdiomas disponibles
                ==============================
                1. Deutsch  (de)
                2. English  (en)
                3. Español  (es)
                4. Français (fr)
                5. Italiano (it)
                \nPor favor, selecciona una opción mostrada en pantalla:\s""");
        var opcion = obtenerOpcion();

        switch (opcion) {
            case 1 -> listarLibrosPorIdioma("Deutsch", "de");
            case 2 -> listarLibrosPorIdioma("English", "en");
            case 3 -> listarLibrosPorIdioma("Español", "es");
            case 4 -> listarLibrosPorIdioma("Français", "fr");
            case 5 -> listarLibrosPorIdioma("Italiano", "it");
            default -> System.out.println("Opción no válida. Por favor, selecciona un número del 1 al 5.");
        }
    }

    private void listarLibrosPorIdioma(String nombre, String idioma) {
        List<Libro> libros = libroRepository.findByIdioma(idioma);

        if (libros.isEmpty()) {
            System.out.println("\nNo se encontraron libros en " + nombre + ".");
        } else {
            System.out.println("\nLibros en " + nombre + ":");
            System.out.println("==============================");
            libros.stream()
                    .sorted(Comparator.comparing(Libro::getDescargas).reversed())
                    .forEach(System.out::println);
        }
    }
}
