package com.aluracursos.literatura.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aluracursos.literatura.modelo.Autor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface RepositorioAutor extends JpaRepository<Autor, Long> {
    List<Autor> findAll();
    @Query("SELECT a FROM Libro l JOIN l.autor a WHERE a.nombre LIKE %:nombre%")
    List<Autor> buscarAutoresPorNombre(@Param("nombre") String nombre);
    @Query("SELECT a FROM Autor a WHERE a.nacimiento   <= :fecha AND a.fallecimiento > :fecha")
    List<Autor> listarAutoresVivos(@Param("fecha") Integer fecha);

    Optional<Autor> findByNombre(String s);
}
