package com.aluracursos.literatura.principal;

import com.aluracursos.literatura.modelo.*;
import com.aluracursos.literatura.repositorio.RepositorioAutor;
import com.aluracursos.literatura.repositorio.RepositorioLibros;
import com.aluracursos.literatura.servicio.ConsumoApi;
import com.aluracursos.literatura.servicio.ConvierteDatos;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
@Service
public class Principal {
    private final RepositorioAutor repositorioAutor;
    private Scanner teclado = new Scanner(System.in);
    private final String URL = "https://gutendex.com/books/?search=";
    private ConsumoApi consumoApi = new ConsumoApi();
    private ConvierteDatos conversor = new ConvierteDatos();
    private RepositorioLibros repositorio;
    private List<Libro> libros;
    private Optional<Libro> LibroBuscado;

    public Principal(RepositorioLibros repositorio, RepositorioAutor repositorioAutor) {
        this.repositorio = repositorio;
        this.repositorioAutor = repositorioAutor;
    }

    public void mostrarMenu() {
        var opcion = -1;
        var menu = """
                ---------------------------------------
                Elija la opcion a través de su numero:
                1 - Buscar libro por titulo
                2 - Lista de libros registrados
                3 - Lista de autores registrados
                4 - Listar autores vivos en un determinado año
                5 - Listar libros por idioma
                6 - Top 5 libros
                7 - Buscar autor por nombre
                0 - Salir
                """;
        while (opcion != 0) {
            System.out.println(menu);
            try {
                opcion = Integer.valueOf(teclado.nextLine());
                switch (opcion) {
                    case 1:
                        buscarLibroPorTitulo();
                        break;
                    case 2:
                        listarLibrosRegistrados();
                        break;
                    case 3:
                        listarTodosLosAutores();
                        break;
                    case 4:
                        listarAutoresVivos();
                        break;

                    case 5:
                        listarLibrosPorIdioma();
                        break;
                    case 6:
                        top5Libros();
                        break;
                    case 7:
                        buscarAutoresPorNombre();
                        break;
                    case 0:
                        System.out.println("Gracias por usar LiterAlura");
                        System.out.println("Cerrando la aplicacion...");
                        break;
                    default:
                        System.out.println("Opcion no valida!");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Opcion no valida: " + e.getMessage());

            }
        }
    }




    private void buscarLibroPorTitulo() {
        System.out.println("Ingrese el titulo del libro a buscar: ");
        var titulo = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL + titulo.replace(" ", "+").toLowerCase());
        var datos = conversor.obtenerDatos(json, Datos.class);

        Optional<InformacionLibro> libroOpt = datos.libros().stream().findFirst();
        if (libroOpt.isPresent()) {
            InformacionLibro primerLibro = libroOpt.get();
            System.out.println();
            System.out.println("Título: " + primerLibro.titulo());
            System.out.println("Autores: " + primerLibro.autorList().stream()
                    .map(a -> a.nombreAutor())
                    .collect(Collectors.joining(", ")));
            System.out.println("Idiomas: " + String.join(", ", primerLibro.lenguage()));
            System.out.println("Total de descargas: " + primerLibro.totalDescargas());

            // Verifica si el libro ya existe en la base de datos
            if (repositorio.existsByTitulo(primerLibro.titulo())) {
                System.out.println("El libro ya existe en la base de datos.");

            } else {
                Libro libro = new Libro();
                libro.setTitulo(primerLibro.titulo());

                // Asumiendo que hay al menos un idioma
                if (!primerLibro.lenguage().isEmpty()) {
                    try {
                        libro.setLenguaje(Lenguaje.valueOf(String.valueOf(Lenguaje.fromString(primerLibro.lenguage().get(0)))));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Error al convertir el lenguaje: " + e.getMessage());
                        // Puedes asignar un valor por defecto o manejar el error de alguna otra manera
                    }
                } else {
                    System.out.println("No se encontró un lenguaje para el libro.");
                }

                try {
                    libro.setNumeroDeDescargas(Double.valueOf(primerLibro.totalDescargas()));
                } catch (NumberFormatException e) {
                    System.out.println("Error al convertir el número de descargas: " + e.getMessage());
                    libro.setNumeroDeDescargas(0.0); // Asigna un valor por defecto o maneja el error adecuadamente
                }

                for (DatosAutor datosAutor : primerLibro.autorList()) {
                    // Busca si el autor ya existe
                    Autor autor = repositorioAutor.findByNombre(datosAutor.nombreAutor())
                            .orElseGet(() -> {
                                Autor nuevoAutor = new Autor();
                                nuevoAutor.setNombre(datosAutor.nombreAutor());
                                nuevoAutor.setNacimiento(datosAutor.nacimiento());
                                nuevoAutor.setFallecimiento(datosAutor.fallecimiento());
                                return nuevoAutor;
                            });

                    // Asociar el autor al libro
                    libro.setAutor(autor);
                    if (autor.getLibros() == null) {
                        autor.setLibros(new ArrayList<>());
                    }
                    autor.getLibros().add(libro);

                    // Guarda el autor (esto también guardará el libro debido a la relación bidireccional)
                    repositorioAutor.save(autor);
                }

                // Guarda el libro en la base de datos
                repositorio.save(libro);
                System.out.println("Libro guardado en la base de datos.");
            }
        } else {
            System.out.println("No se encontraron libros con ese título.");
        }
    }

    private void listarLibrosRegistrados() {
        // Recuperar la lista de libros desde el repositorio
        List<Libro> libros = repositorio.librosRegistrados();



        if (libros.isEmpty()) {
            System.out.println("No hay libros registrados.");
        } else {
            libros.forEach(libro -> {
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autor: " + (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"));
                System.out.println("Lenguaje: " + libro.getLenguaje());
                System.out.println("Número de Descargas: " + libro.getNumeroDeDescargas());
                System.out.println("------------------------------");
                System.out.println();
            });
        }
    }

    private void listarTodosLosAutores() {
        List<Autor> autores = repositorioAutor.findAll();


        if (autores.isEmpty()) {
            System.out.println("No hay autores registrados.");
        } else {
            for (Autor autor : autores) {
                System.out.println("Nombre: " + autor.getNombre());
                System.out.println("Nacimiento: " + autor.getNacimiento());
                System.out.println("Fallecimiento: " + autor.getFallecimiento());
                List<Libro> libros = autor.getLibros();
                //System.out.println("Libros: ", libros.forEach(libro -> System.out.println("   " + libro)));
                System.out.println("Libros:");
                libros.forEach(libro -> System.out.println("       " + libro));
                System.out.println("---------------------------------");
            }
        }
    }

    private void listarAutoresVivos() {
        System.out.println("Ingrese el año para verificar qué autores estaban vivos:");
        int año = Integer.parseInt(teclado.nextLine());

        List<Autor> autoresVivos = repositorioAutor.listarAutoresVivos(año);

        if (autoresVivos.isEmpty()) {
            System.out.println("No hay autores vivos en el año " + año + ".");
        } else {
            autoresVivos.forEach(autor -> {
                System.out.println("Nombre: " + autor.getNombre());
                System.out.println("Nacimiento: " + autor.getNacimiento());
                System.out.println("Fallecimiento: " + autor.getFallecimiento());
                System.out.println("------------------------------");
            });
        }
    }

    private void listarLibrosPorIdioma() {
        System.out.println("Ingrese el idioma del libro (es, en, fr, pt, de):");
        String idiomaInput = teclado.nextLine().trim().toUpperCase();

        // Convertir el texto del usuario a un valor del enum Lenguaje
        Lenguaje lenguaje;
        try {
            lenguaje = Lenguaje.valueOf(idiomaInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Idioma no válido. Asegúrese de ingresar uno de los siguientes: es, en, fr, pt, de.");
            return;
        }

        // Consultar el repositorio para obtener los libros en el idioma especificado
        List<Libro> libros = repositorio.librosPorIdioma(lenguaje);

        if (libros.isEmpty()) {
            System.out.println("No hay libros disponibles en el idioma " + lenguaje.getIdioma() + ".");
        } else {
            libros.forEach(libro -> {
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autor: " + (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"));
                System.out.println("Lenguaje: " + libro.getLenguaje().getIdioma());
                System.out.println("Número de Descargas: " + libro.getNumeroDeDescargas());
                System.out.println("------------------------------");
            });
        }
    }

    private void top5Libros() {
        // Obtén los 5 libros con más descargas
        List<Libro> topLibros = repositorio.findTop5ByNumeroDeDescargasDesc();

        if (topLibros.isEmpty()) {
            System.out.println("No hay libros disponibles para mostrar.");
        } else {
            System.out.println("Top 5 Libros por Número de Descargas:");
            topLibros.stream().limit(5).forEach(libro -> {
                System.out.println("------------------------------");
                System.out.println("Título: " + libro.getTitulo());
                System.out.println("Autor: " + (libro.getAutor() != null ? libro.getAutor().getNombre() : "Desconocido"));
                System.out.println("Lenguaje: " + libro.getLenguaje());
                System.out.println("Número de Descargas: " + libro.getNumeroDeDescargas());
                System.out.println("------------------------------");
            });
        }
    }


    private void buscarAutoresPorNombre() {
        System.out.println("Ingrese el nombre del autor:");
        String nombre = teclado.nextLine();
        List<Autor> autores = repositorioAutor.buscarAutoresPorNombre(nombre);

        if (autores.isEmpty()) {
            System.out.println("No se encontraron autores con ese nombre.");
        } else {
            autores.forEach(autor -> {
                System.out.println("Nombre: " + autor.getNombre());
                System.out.println("Nacimiento: " + autor.getNacimiento());
                System.out.println("Fallecimiento: " + autor.getFallecimiento());
                System.out.println();
            });
        }
    }

}
