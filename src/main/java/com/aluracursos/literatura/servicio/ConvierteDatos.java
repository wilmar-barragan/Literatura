package com.aluracursos.literatura.servicio;

import com.aluracursos.literatura.modelo.InformacionLibro;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ConvierteDatos implements IConvierteDatos{
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T obtenerDatos(String json, Class<T> clase) {
        try {
            return objectMapper.readValue(json, clase);
        } catch (JsonProcessingException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public List<InformacionLibro> obtenerDatos(String json) {
        return List.of();
    }

}
