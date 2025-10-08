package juego.personajes;

import juego.elementos.Carta;

public class Jugador {
    private String nombre;
    private int puntos;
    private Carta mano[] = new Carta[3];
    private int cantCartas = 0;

    // ✅ NUEVO: Puntos de la ronda actual (antes de sumarlos al total)
    private int puntosRondaActual = 0;

    public Jugador() {
        this.puntos = 0;
        this.nombre = "Jugador";
    }

    public Jugador(String nombre) {
        this.nombre = nombre;
        this.puntos = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Carta[] getMano() {
        return mano;
    }

    public int getPuntos() {
        return puntos;
    }

    // ✅ NUEVO: Sumar puntos
    public void sumarPuntos(int cantidad) {
        this.puntos += cantidad;
    }

    // ✅ NUEVO: Establecer puntos directamente
    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    // ✅ NUEVO: Resetear puntos (para nueva partida)
    public void resetearPuntos() {
        this.puntos = 0;
        this.puntosRondaActual = 0;
    }

    // ✅ NUEVO: Gestión de puntos de ronda
    public void setPuntosRondaActual(int puntos) {
        this.puntosRondaActual = puntos;
    }

    public int getPuntosRondaActual() {
        return puntosRondaActual;
    }

    public void confirmarPuntosRonda() {
        this.puntos += puntosRondaActual;
        this.puntosRondaActual = 0;
    }

    public void agregarCarta(Carta c){
        if (cantCartas < mano.length) {
            this.mano[cantCartas] = c;
            cantCartas++;
        }
    }

    public void limpiarMazo(){
        // Limpiar referencias
        for (int i = 0; i < mano.length; i++) {
            mano[i] = null;
        }
        cantCartas = 0;
    }

    // ✅ NUEVO: Obtener cantidad de cartas en mano
    public int getCantidadCartas() {
        return cantCartas;
    }

    // ✅ NUEVO: Verificar si tiene una carta específica
    public boolean tieneCarta(Carta carta) {
        for (Carta c : mano) {
            if (c != null && c.equals(carta)) {
                return true;
            }
        }
        return false;
    }
}