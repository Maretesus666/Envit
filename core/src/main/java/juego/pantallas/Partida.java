package juego.pantallas;

import com.badlogic.gdx.Gdx;
import juego.elementos.*;
import juego.personajes.Jugador;
import juego.personajes.RivalBot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Partida {

    private ArrayList<Carta> mazoRevuelto = new ArrayList<>();
    private int indiceMazo = 0;

    // ✅ NUEVO: Enum para identificar jugadores
    public enum TipoJugador { JUGADOR_1, JUGADOR_2 }

    private enum EstadoTurno { ESPERANDO_JUGADOR_1, ESPERANDO_JUGADOR_2, FINALIZANDO_MANO, PARTIDA_TERMINADA }
    private EstadoTurno estadoActual;

    // ✅ NUEVO: Control de fin de partida
    private final int PUNTOS_PARA_GANAR = 15;
    private Jugador ganador = null;

    private ZonaJuego zonaJugador1;  // antes zonaJugador
    private ZonaJuego zonaJugador2;  // antes zonaRival

    private RivalBot rivalBot;  // null cuando sea online
    private Jugador jugador1;   // El jugador humano local
    private Jugador jugador2;   // El rival (bot o jugador remoto)

    private int cartasJugador1Antes = 0;
    private int cartasJugador2Antes = 0;

    private float delayFinalizacion = 0;
    private boolean esperandoFinalizacion = false;

    private int manoActual = 0;
    private final int MAX_MANOS = 3;

    // ✅ NUEVO: Control de quién es mano
    private TipoJugador jugadorMano;  // Quién empieza la ronda
    private Random random = new Random();

    // ✅ NUEVO: Sistema de Truco
    private boolean trucoUsado = false;
    private int manoTrucoUsada = -1;  // En qué mano se usó el truco (-1 = no usado)
    private TipoJugador jugadorQueCanto = null;  // Quién cantó truco

    public Partida() {
        // Crear y mezclar el mazo al inicio
        Mazo mazoOriginal = new Mazo();
        for (int i = 0; i < mazoOriginal.getCantCartas(); i++) {
            mazoRevuelto.add(mazoOriginal.getCarta(i));
        }
        Collections.shuffle(mazoRevuelto);
    }

    /**
     * Inicializar la partida con los jugadores y zonas
     * @param bot puede ser null si es modo online
     */
    public void inicializar(ZonaJuego zonaJug1, ZonaJuego zonaJug2, RivalBot bot,
                            Jugador jug1, Jugador jug2, int manoActual) {
        this.zonaJugador1 = zonaJug1;
        this.zonaJugador2 = zonaJug2;
        this.rivalBot = bot;
        this.jugador1 = jug1;
        this.jugador2 = jug2;

        // ✅ NUEVO: Determinar quién empieza de forma aleatoria
        this.jugadorMano = random.nextBoolean() ? TipoJugador.JUGADOR_1 : TipoJugador.JUGADOR_2;

        // Setear el estado inicial según quién sea mano
        this.estadoActual = (jugadorMano == TipoJugador.JUGADOR_1)
                ? EstadoTurno.ESPERANDO_JUGADOR_1
                : EstadoTurno.ESPERANDO_JUGADOR_2;

        System.out.println("INICIO DE PARTIDA - Empieza: " +
                (jugadorMano == TipoJugador.JUGADOR_1 ? jug1.getNombre() : jug2.getNombre()));

        this.cartasJugador1Antes = 0;
        this.cartasJugador2Antes = 0;
        this.manoActual = manoActual;

        // ✅ NUEVO: Resetear truco
        this.trucoUsado = false;
        this.manoTrucoUsada = -1;
        this.jugadorQueCanto = null;

        // Si empieza el jugador 2 y es un bot, activarlo
        if (jugadorMano == TipoJugador.JUGADOR_2 && rivalBot != null) {
            rivalBot.activarTurno();
        }
    }

    public void finalizarRonda(Jugador jugador1, Jugador jugador2) {
        jugador1.limpiarMazo();
        jugador2.limpiarMazo();

        // Limpiar zonas
        if (zonaJugador1 != null) zonaJugador1.limpiar();
        if (zonaJugador2 != null) zonaJugador2.limpiar();

        // Revolver de nuevo para la próxima ronda
        indiceMazo = 0;
        Collections.shuffle(mazoRevuelto);

        // ✅ NUEVO: Alternar quién es mano
        jugadorMano = (jugadorMano == TipoJugador.JUGADOR_1)
                ? TipoJugador.JUGADOR_2
                : TipoJugador.JUGADOR_1;

        estadoActual = (jugadorMano == TipoJugador.JUGADOR_1)
                ? EstadoTurno.ESPERANDO_JUGADOR_1
                : EstadoTurno.ESPERANDO_JUGADOR_2;

        System.out.println("NUEVA RONDA - Empieza: " +
                (jugadorMano == TipoJugador.JUGADOR_1 ? this.jugador1.getNombre() : this.jugador2.getNombre()));

        cartasJugador1Antes = 0;
        cartasJugador2Antes = 0;
        manoActual = 0;
        repartirCartas(jugador1, jugador2);

        // Si empieza el jugador 2 y es un bot, activarlo
        if (jugadorMano == TipoJugador.JUGADOR_2 && rivalBot != null) {
            rivalBot.activarTurno();
        }
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

    /**
     * Update de la lógica de turnos (llamar cada frame)
     */
    public void update(float delta) {
        if (zonaJugador1 == null || zonaJugador2 == null) {
            return;
        }

        // Actualizar el bot si existe
        if (rivalBot != null) {
            rivalBot.update(delta);
        }

        // Si estamos esperando que se vea la última carta antes de finalizar
        if (esperandoFinalizacion) {
            delayFinalizacion += delta;
            if (delayFinalizacion >= 1.5f) {
                estadoActual = EstadoTurno.FINALIZANDO_MANO;
                esperandoFinalizacion = false;
                delayFinalizacion = 0;
            } else {
                return;
            }
        }

        // Gestionar turnos
        switch (estadoActual) {
            case ESPERANDO_JUGADOR_1:
                int cartasJug1Actual = zonaJugador1.getCantidadCartas();

                if (cartasJug1Actual > cartasJugador1Antes) {
                    System.out.println(jugador1.getNombre() + " tiró una carta. Turno de " + jugador2.getNombre());
                    cartasJugador1Antes = cartasJug1Actual;
                    estadoActual = EstadoTurno.ESPERANDO_JUGADOR_2;

                    // ✅ NUEVO: Solo activar el bot si existe
                    if (rivalBot != null) {
                        rivalBot.activarTurno();
                    }
                    // TODO: Cuando sea online, aquí enviarías señal al servidor
                }
                break;

            case ESPERANDO_JUGADOR_2:
                // ✅ MODIFICADO: Verificar si el jugador 2 terminó su turno
                boolean turnoJugador2Completo = false;

                if (rivalBot != null) {
                    // Modo bot: esperar a que el bot termine
                    turnoJugador2Completo = !rivalBot.isEsperandoTurno();
                } else {
                    // Modo online: verificar directamente las cartas en la zona
                    // TODO: En online, aquí esperarías respuesta del servidor
                    turnoJugador2Completo = zonaJugador2.getCantidadCartas() > cartasJugador2Antes;
                }

                if (turnoJugador2Completo) {
                    int cartasJug2Actual = zonaJugador2.getCantidadCartas();

                    if (cartasJug2Actual > cartasJugador2Antes) {
                        System.out.println(jugador2.getNombre() + " tiró una carta. Turno de " + jugador1.getNombre());
                        cartasJugador2Antes = cartasJug2Actual;
                        manoActual++;

                        if (manoActual >= MAX_MANOS) {
                            esperandoFinalizacion = true;
                            delayFinalizacion = 0;
                        } else {
                            estadoActual = EstadoTurno.ESPERANDO_JUGADOR_1;
                        }
                    }
                }
                break;

            case FINALIZANDO_MANO:
                evaluarRonda();
                break;

            case PARTIDA_TERMINADA:
                // No hacer nada, la partida terminó
                break;
        }
    }

    /**
     * Evalúa quién ganó la ronda
     */
    private void evaluarRonda() {
        System.out.println("\n=== EVALUANDO RONDA ===");
        System.out.println("Cartas " + jugador1.getNombre() + " en zona: " + zonaJugador1.getCantidadCartas());
        System.out.println("Cartas " + jugador2.getNombre() + " en zona: " + zonaJugador2.getCantidadCartas());

        ArrayList<Carta> cartasJug1 = zonaJugador1.getCartasJugadas();
        ArrayList<Carta> cartasJug2 = zonaJugador2.getCartasJugadas();

        System.out.println("\nCartas de " + jugador1.getNombre() + ":");
        for (Carta c : cartasJug1) {
            System.out.println("  - " + c.getNombre() + " (Jerarquía: " + c.getJerarquia() + ")");
        }

        System.out.println("\nCartas de " + jugador2.getNombre() + ":");
        for (Carta c : cartasJug2) {
            System.out.println("  - " + c.getNombre() + " (Jerarquía: " + c.getJerarquia() + ")");
        }

        // Evaluar cada mano
        for (int i = 0; i < Math.min(cartasJug1.size(), cartasJug2.size()); i++) {
            Carta cartaJug1 = cartasJug1.get(i);
            Carta cartaJug2 = cartasJug2.get(i);

            // ✅ NUEVO: Calcular puntos según si hay truco
            int puntosEnJuego = 1;
            if (trucoUsado && manoTrucoUsada == i) {
                puntosEnJuego = 2;
                System.out.println("¡TRUCO! Esta mano vale " + puntosEnJuego + " puntos");
            }

            // Jerarquía menor = carta más fuerte
            if (cartaJug1.getJerarquia() < cartaJug2.getJerarquia()) {
                jugador1.sumarPuntos(puntosEnJuego);
                System.out.println("Mano " + (i+1) + ": GANÓ " + jugador1.getNombre() +
                        " (+" + puntosEnJuego + " puntos)");
            } else if (cartaJug1.getJerarquia() > cartaJug2.getJerarquia()) {
                jugador2.sumarPuntos(puntosEnJuego);
                System.out.println("Mano " + (i+1) + ": GANÓ " + jugador2.getNombre() +
                        " (+" + puntosEnJuego + " puntos)");
            } else {
                System.out.println("Mano " + (i+1) + ": EMPATE (parda)");
            }
        }

        System.out.println("\nResultado: " + jugador1.getNombre() + " " +
                jugador1.getPuntos() + " - " +
                jugador2.getPuntos() + " " + jugador2.getNombre());

        // ✅ NUEVO: Verificar si algún jugador llegó a 15 puntos
        if (jugador1.getPuntos() >= PUNTOS_PARA_GANAR) {
            ganador = jugador1;
            estadoActual = EstadoTurno.PARTIDA_TERMINADA;
            System.out.println("\n🏆 ¡GANADOR: " + jugador1.getNombre() + "!");
        } else if (jugador2.getPuntos() >= PUNTOS_PARA_GANAR) {
            ganador = jugador2;
            estadoActual = EstadoTurno.PARTIDA_TERMINADA;
            System.out.println("\n🏆 ¡GANADOR: " + jugador2.getNombre() + "!");
        }
    }

    /**
     * ✅ NUEVO: Método para saber si es el turno del jugador 1 (humano local)
     */
    public boolean esTurnoJugador1() {
        return estadoActual == EstadoTurno.ESPERANDO_JUGADOR_1;
    }

    /**
     * ✅ MODIFICADO: Mantener compatibilidad con código existente
     */
    public boolean esTurnoJugador() {
        return esTurnoJugador1();
    }

    /**
     * Método para saber si es el turno del jugador 2
     */
    public boolean esTurnoJugador2() {
        return estadoActual == EstadoTurno.ESPERANDO_JUGADOR_2;
    }

    /**
     * ✅ MODIFICADO: Mantener compatibilidad
     */
    public boolean esTurnoRival() {
        return esTurnoJugador2();
    }

    /**
     * Método para saber si la ronda terminó
     */
    public boolean rondaTerminada() {
        return estadoActual == EstadoTurno.FINALIZANDO_MANO && ganador == null;
    }

    /**
     * ✅ NUEVO: Método para saber si la partida completa terminó
     */
    public boolean partidaTerminada() {
        return estadoActual == EstadoTurno.PARTIDA_TERMINADA;
    }

    /**
     * ✅ NUEVO: Obtener el ganador de la partida
     */
    public Jugador getGanador() {
        return ganador;
    }

    /**
     * Método para iniciar una nueva ronda
     */
    public void nuevaRonda() {
        // ✅ NUEVO: Alternar quién es mano
        jugadorMano = (jugadorMano == TipoJugador.JUGADOR_1)
                ? TipoJugador.JUGADOR_2
                : TipoJugador.JUGADOR_1;

        estadoActual = (jugadorMano == TipoJugador.JUGADOR_1)
                ? EstadoTurno.ESPERANDO_JUGADOR_1
                : EstadoTurno.ESPERANDO_JUGADOR_2;

        System.out.println("NUEVA RONDA - Empieza: " +
                (jugadorMano == TipoJugador.JUGADOR_1 ? jugador1.getNombre() : jugador2.getNombre()));

        cartasJugador1Antes = 0;
        cartasJugador2Antes = 0;
        manoActual = 0;

        if (zonaJugador1 != null) zonaJugador1.limpiar();
        if (zonaJugador2 != null) zonaJugador2.limpiar();

        // ✅ NUEVO: Resetear truco para la nueva ronda
        trucoUsado = false;
        manoTrucoUsada = -1;
        jugadorQueCanto = null;

        // Si empieza el jugador 2 y es un bot, activarlo
        if (jugadorMano == TipoJugador.JUGADOR_2 && rivalBot != null) {
            rivalBot.activarTurno();
        }
    }

    public int getCartasRestantes() {
        return mazoRevuelto.size() - indiceMazo;
    }

    public int getManoActual() {
        return manoActual;
    }

    /**
     * ✅ NUEVO: Método para saber quién es mano
     */
    public TipoJugador getJugadorMano() {
        return jugadorMano;
    }

    /**
     * ✅ NUEVO: Para cuando sea online y quieras forzar el turno inicial
     */
    public void setJugadorMano(TipoJugador jugador) {
        this.jugadorMano = jugador;
        estadoActual = (jugador == TipoJugador.JUGADOR_1)
                ? EstadoTurno.ESPERANDO_JUGADOR_1
                : EstadoTurno.ESPERANDO_JUGADOR_2;
    }

    // ===== MÉTODOS DEL SISTEMA DE TRUCO =====

    /**
     * ✅ NUEVO: Cantar truco (solo se puede usar una vez por ronda)
     * @param jugador Quién canta el truco
     * @return true si se pudo cantar, false si ya fue usado
     */
    public boolean cantarTruco(TipoJugador jugador) {
        if (trucoUsado) {
            System.out.println("El truco ya fue cantado en esta ronda");
            return false;
        }

        trucoUsado = true;
        manoTrucoUsada = manoActual;
        jugadorQueCanto = jugador;

        String nombreJugador = (jugador == TipoJugador.JUGADOR_1)
                ? jugador1.getNombre()
                : jugador2.getNombre();

        System.out.println("¡" + nombreJugador + " CANTÓ TRUCO! La mano " +
                (manoActual + 1) + " vale 2 puntos");

        return true;
    }

    /**
     * ✅ NUEVO: Verificar si el truco ya fue usado
     */
    public boolean isTrucoUsado() {
        return trucoUsado;
    }

    /**
     * ✅ NUEVO: Verificar si el truco está activo en la mano actual
     */
    public boolean isTrucoActivoEnManoActual() {
        return trucoUsado && manoTrucoUsada == manoActual;
    }

    /**
     * ✅ NUEVO: Obtener quién cantó el truco
     */
    public TipoJugador getJugadorQueCanto() {
        return jugadorQueCanto;
    }

    /**
     * ✅ NUEVO: Obtener en qué mano se usó el truco
     */
    public int getManoTrucoUsada() {
        return manoTrucoUsada;
    }
}