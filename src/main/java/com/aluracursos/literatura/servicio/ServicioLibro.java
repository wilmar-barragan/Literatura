package com.aluracursos.literatura.servicio;
import com.aluracursos.literatura.modelo.Autor;
import com.aluracursos.literatura.modelo.Libro;
import com.aluracursos.literatura.repositorio.RepositorioAutor;
import com.aluracursos.literatura.repositorio.RepositorioLibros;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ServicioLibro {

    @Autowired
    private RepositorioLibros repositorioLibros;

    @Autowired
    private RepositorioAutor repositorioAutores;

    @Transactional
    public void guardarLibro(Libro libro, Autor autor) {
        autor.addLibro(libro); // Asocia el libro con el autor
        repositorioAutores.save(autor); // Guarda el autor junto con sus libros
    }
}
