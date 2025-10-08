package juego.personajes;

import juego.elementos.Carta;
import juego.elementos.ZonaJuego;

import java.util.Random;

/**
 * Bot provisorio para simular un rival.
 * Tira cartas de forma automática con un delay.
 *
 * TODO: Reemplazar con sistema online en la versión final.
 */
public class RivalBot {

    private Jugador jugador;
    private ZonaJuego zonaJuego;
    private Random random;

    // Estado del bot
    private boolean esperandoTurno = false;
    private float tiempoEspera = 0f;
    private float DELAY_ENTRE_CARTAS = 2.0f; // 2 segundos entre cada carta

    // Índice de la carta que va a jugar
    private int siguienteCartaIndex = 0;

    public RivalBot(Jugador jugador, ZonaJuego zona) {
        this.jugador = jugador;
        this.zonaJuego = zona;
        this.random = new Random();
    }

    /**
     * Actualiza el estado del bot (llamar en cada frame)
     */
    public void update(float delta) {
        if (!esperandoTurno) {
            return;
        }

        tiempoEspera += delta;

        // Cuando pasa el tiempo de espera, tira una carta
        if (tiempoEspera >= DELAY_ENTRE_CARTAS) {
            tirarCarta();
            tiempoEspera = 0f;
            esperandoTurno = false;
        }
    }

    /**
     * Activa el turno del bot para que tire una carta
     */
    public void activarTurno() {
        if (todasLasCartasJugadas()) {
            System.out.println("El bot ya jugó todas sus cartas");
            return;
        }

        esperandoTurno = true;
        tiempoEspera = 0f;
    }

    /**
     * El bot elige y tira una carta (lógica súper simple)
     */
    private void tirarCarta() {
        Carta[] mano = jugador.getMano();

        // Buscar la primera carta disponible (que no esté en la zona)
        for (int i = 0; i < mano.length; i++) {
            Carta carta = mano[i];

            if (carta != null && !zonaJuego.contieneCartaJugada(carta)) {
                // Tirar esta carta
                zonaJuego.agregarCarta(carta);
                System.out.println("Bot jugó: " + carta.getNombre());
                return;
            }
        }
    }

    /**
     * Tira una carta aleatoria (estrategia alternativa)
     */
    public void tirarCartaAleatoria() {
        Carta[] mano = jugador.getMano();

        // Hacer una lista de cartas disponibles
        int[] disponibles = new int[3];
        int count = 0;

        for (int i = 0; i < mano.length; i++) {
            if (mano[i] != null && !zonaJuego.contieneCartaJugada(mano[i])) {
                disponibles[count++] = i;
            }
        }

        if (count > 0) {
            // Elegir una al azar
            int indexAleatorio = disponibles[random.nextInt(count)];
            Carta carta = mano[indexAleatorio];
            zonaJuego.agregarCarta(carta);
            System.out.println("Bot jugó (aleatorio): " + carta.getNombre());
        }
    }

    /**
     * Verifica si el bot ya jugó todas sus cartas
     */
    public boolean todasLasCartasJugadas() {
        Carta[] mano = jugador.getMano();

        for (Carta carta : mano) {
            if (carta != null && !zonaJuego.contieneCartaJugada(carta)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Obtiene cuántas cartas jugó el bot
     */
    public int getCartasJugadas() {
        int count = 0;
        Carta[] mano = jugador.getMano();

        for (Carta carta : mano) {
            if (carta != null && zonaJuego.contieneCartaJugada(carta)) {
                count++;
            }
        }

        return count;
    }

    /**
     * Reinicia el estado del bot para una nueva ronda
     */
    public void reset() {
        esperandoTurno = false;
        tiempoEspera = 0f;
        siguienteCartaIndex = 0;
    }

    /**
     * Cambia el delay entre cartas (para hacerlo más rápido/lento)
     */
    public void setDelay(float segundos) {
        this.DELAY_ENTRE_CARTAS = segundos;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public boolean isEsperandoTurno() {
        return esperandoTurno;
    }
}