package com.aluracursos.literatura.repositorio;


import com.aluracursos.literatura.modelo.Lenguaje;
import com.aluracursos.literatura.modelo.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RepositorioLibros extends JpaRepository<Libro, Long> {
    @Query("SELECT l FROM Autor a JOIN a.libros l ORDER BY l.titulo")
    List<Libro> librosRegistrados();

    @Query("SELECT l FROM Autor a JOIN a.libros l WHERE l.lenguaje = :lenguaje")
    List<Libro> librosPorIdioma(@Param("lenguaje") Lenguaje lenguaje);

    boolean existsByTitulo(String titulo);

    @Query("SELECT l FROM Libro l ORDER BY l.numeroDeDescargas DESC")
    List<Libro> findTop5ByNumeroDeDescargasDesc();

}
