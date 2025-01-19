package com.aluracursos.literatura;

import com.aluracursos.literatura.principal.Principal;
import com.aluracursos.literatura.repositorio.RepositorioAutor;
import com.aluracursos.literatura.repositorio.RepositorioLibros;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LiteraturaApplication implements CommandLineRunner {
	private final RepositorioLibros repositorioLibros;
	private final RepositorioAutor repositorioAutor;

	@Autowired
	public LiteraturaApplication(RepositorioLibros repositorioLibros, RepositorioAutor repositorioAutor) {
		this.repositorioLibros = repositorioLibros;
		this.repositorioAutor = repositorioAutor;
	}
	public static void main(String[] args) {SpringApplication.run(LiteraturaApplication.class, args);}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repositorioLibros, repositorioAutor);
		principal.mostrarMenu();
	}
}
