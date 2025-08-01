package com.Envit.Juego.elementos;

public enum Palo {
    BASTO("Basto"), ORO("Oro"), ESPADAS("Espadas"), COPAS("Copas");

    private String nombre;
    Palo(String nombre) {}

    public String getNombre() {
        return nombre;
    }
}
