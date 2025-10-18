package juego.pantallas;

import com.badlogic.gdx.Gdx;
import juego.elementos.*;
import juego.personajes.Jugador;
import juego.personajes.RivalBot;
import juego.utilidades.Consola;

import java.util.ArrayList;
import java.util.Collections;

public class Partida {

    private ArrayList<Carta> mazoRevuelto = new ArrayList<>();
    private int indiceMazo = 0;

    private enum EstadoTurno { ESPERANDO_JUGADOR, ESPERANDO_RIVAL, FINALIZANDO_MANO }
    private EstadoTurno estadoActual = EstadoTurno.ESPERANDO_JUGADOR;

    private ZonaJuego zonaJugador;
    private ZonaJuego zonaRival;
    private RivalBot rivalBot;
    private Jugador jugadorHumano;
    private Jugador jugadorRival;;
    private float delayFinalizacion = 0;
    private boolean esperandoFinalizacion = false;

    private int cartasJugadorAntes = 0;
    private int cartasRivalAntes = 0;

    private int manoActual = 0;
    private final int MAX_MANOS = 3;

    public Partida() {
        // Crear y mezclar el mazo al inicio
        Mazo mazoOriginal = new Mazo();
        for (int i = 0; i < mazoOriginal.getCantCartas(); i++) {
            mazoRevuelto.add(mazoOriginal.getCarta(i));
        }
        Collections.shuffle(mazoRevuelto);
    }

    // ✅ MEJORADO: Vincular las zonas, el bot Y los jugadores
    public void inicializar(ZonaJuego zonaJug, ZonaJuego zonaRiv, RivalBot bot,
                            Jugador jugHumano, Jugador jugRival, int manoActual) {
        this.zonaJugador = zonaJug;
        this.zonaRival = zonaRiv;
        this.rivalBot = bot;
        this.jugadorHumano = jugHumano;
        this.jugadorRival = jugRival;
        this.estadoActual = EstadoTurno.ESPERANDO_JUGADOR;
        this.cartasJugadorAntes = 0;
        this.cartasRivalAntes = 0;
        this.manoActual = manoActual;
    }


    public void finalizarRonda(Jugador jugador1, Jugador jugador2) {
        jugador1.limpiarMazo();
        jugador2.limpiarMazo();

        // Limpiar zonas
        if (zonaJugador != null) zonaJugador.limpiar();
        if (zonaRival != null) zonaRival.limpiar();

        // Revolver de nuevo para la próxima ronda
        indiceMazo = 0;
        Collections.shuffle(mazoRevuelto);

        // Reiniciar turnos
        estadoActual = EstadoTurno.ESPERANDO_JUGADOR;
        cartasJugadorAntes = 0;
        cartasRivalAntes = 0;
        manoActual = 0;
        repartirCartas(jugador1, jugador2);
    }

    public void repartirCartas(Jugador jugador1, Jugador jugador2) {
        if (indiceMazo + 6 > mazoRevuelto.size()) {
            indiceMazo = 0;
            Collections.shuffle(mazoRevuelto);
        }

        for (int i = 0; i < 3; i++) {
            jugador1.agregarCarta(mazoRevuelto.get(indiceMazo++));
            jugador2.agregarCarta(mazoRevuelto.get(indiceMazo++));
        }
    }

    // ✅ NUEVO: Update de la lógica de turnos (llamar cada frame)
    public void update(float delta) {
        if (zonaJugador == null || zonaRival == null || rivalBot == null) {
            return; // No está inicializado
        }

        // Actualizar el bot
        rivalBot.update(delta);

        // Si estamos esperando que se vea la última carta antes de finalizar
        if (esperandoFinalizacion) {
            delayFinalizacion += delta;
            if (delayFinalizacion >= 1.5f) { // esperar 1.5 segundos aprox.
                estadoActual = EstadoTurno.FINALIZANDO_MANO;
                esperandoFinalizacion = false;
                delayFinalizacion = 0;
            }
            return; // no procesar nada más durante el delay
        }

        // Gestionar turnos
        switch (estadoActual) {
            case ESPERANDO_JUGADOR:
                int cartasJugadorActual = zonaJugador.getCantidadCartas();

                if (cartasJugadorActual > cartasJugadorAntes) {
                    System.out.println("Jugador tiró una carta. Turno del rival.");
                    cartasJugadorAntes = cartasJugadorActual;
                    estadoActual = EstadoTurno.ESPERANDO_RIVAL;

                    rivalBot.activarTurno();
                }
                break;

            case ESPERANDO_RIVAL:
                // Verificar si el rival terminó de jugar
                if (!rivalBot.isEsperandoTurno()) {
                    int cartasRivalActual = zonaRival.getCantidadCartas();

                    if (cartasRivalActual > cartasRivalAntes) {
                        System.out.println("Rival tiró una carta. Turno del jugador.");
                        cartasRivalAntes = cartasRivalActual;
                        manoActual++;

                        if (manoActual >= MAX_MANOS) {
                            // Esperar un momento para mostrar la última carta
                            esperandoFinalizacion = true;
                            delayFinalizacion = 0;
                        } else {
                            estadoActual = EstadoTurno.ESPERANDO_JUGADOR;
                        }
                    }
                }
                break;

            case FINALIZANDO_MANO:
                evaluarRonda();
                finalizarRonda(jugadorHumano, jugadorRival);
                break;
        }
    }

    // ✅ NUEVO: Evalúa quién ganó la ronda
    private void evaluarRonda() {
        System.out.println("\n=== EVALUANDO RONDA ===");
        System.out.println("Cartas jugador en zona: " + zonaJugador.getCantidadCartas());
        System.out.println("Cartas rival en zona: " + zonaRival.getCantidadCartas());

        ArrayList<Carta> cartasJug = zonaJugador.getCartasJugadas();
        ArrayList<Carta> cartasRiv = zonaRival.getCartasJugadas();

        System.out.println("\nCartas del jugador:");
        for (Carta c : cartasJug) {
            System.out.println("  - " + c.getNombre() + " (Jerarquía: " + c.getJerarquia() + ")");
        }

        System.out.println("\nCartas del rival:");
        for (Carta c : cartasRiv) {
            System.out.println("  - " + c.getNombre() + " (Jerarquía: " + c.getJerarquia() + ")");
        }


        for (int i = 0; i < Math.min(cartasJug.size(), cartasRiv.size()); i++) {
            Carta cartaJug = cartasJug.get(i);
            Carta cartaRiv = cartasRiv.get(i);

            // Jerarquía menor = carta más fuerte
            if (cartaJug.getJerarquia() < cartaRiv.getJerarquia()) {
                jugadorHumano.sumarPuntos(1);
                System.out.println("Mano " + (i+1) + ": GANÓ JUGADOR");
            } else if (cartaJug.getJerarquia() > cartaRiv.getJerarquia()) {
                jugadorRival.sumarPuntos(1);
                System.out.println("Mano " + (i+1) + ": GANÓ RIVAL");
            } else {
                System.out.println("Mano " + (i+1) + ": EMPATE (parda)");
            }
        }

        System.out.println("\nResultado: Jugador " + jugadorHumano.getPuntos() + " - " + jugadorRival.getPuntos() + " Rival");



    }

    // ✅ NUEVO: Método para saber si es el turno del jugador
    public boolean esTurnoJugador() {
        return estadoActual == EstadoTurno.ESPERANDO_JUGADOR;
    }

    // ✅ NUEVO: Método para saber si es el turno del rival
    public boolean esTurnoRival() {
        return estadoActual == EstadoTurno.ESPERANDO_RIVAL;
    }

    // ✅ NUEVO: Método para saber si la ronda terminó
    public boolean rondaTerminada() {
        return estadoActual == EstadoTurno.FINALIZANDO_MANO;
    }

    // ✅ NUEVO: Método para iniciar una nueva ronda
    public void nuevaRonda() {
        estadoActual = EstadoTurno.ESPERANDO_JUGADOR;
        cartasJugadorAntes = 0;
        cartasRivalAntes = 0;
        manoActual = 0;

        if (zonaJugador != null) zonaJugador.limpiar();
        if (zonaRival != null) zonaRival.limpiar();
    }

    public int getCartasRestantes() {
        return mazoRevuelto.size() - indiceMazo;
    }

    public int getManoActual() {
        return manoActual;
    }
}