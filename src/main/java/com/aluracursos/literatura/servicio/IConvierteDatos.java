package com.aluracursos.literatura.servicio;
import com.aluracursos.literatura.modelo.InformacionLibro;
import java.util.List;

public interface IConvierteDatos {

    <T> T obtenerDatos(String json, Class<T> clase);

    List<InformacionLibro> obtenerDatos(String json);
}
