package com.aluracursos.literatura.modelo;

public enum Lenguaje {
    ES("es"),
    EN("en"),
    FR("fr"),
    PT("pt"),
    DE("de");

    private final String lenguaje;

    private Lenguaje(String lenguaje) {
        this.lenguaje = lenguaje;
    }

    public String getIdioma() {
        return lenguaje;
    }

    public static Lenguaje fromString(String text) {
        for (Lenguaje lenguaje: Lenguaje.values()) {
            if (lenguaje.lenguaje.equalsIgnoreCase(text)) {
                return lenguaje;
            }
        }
        throw new IllegalArgumentException("No se ha encontrado nada: " + text);
    }
}
