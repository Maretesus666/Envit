package juego.elementos;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.viewport.Viewport;
import juego.personajes.Jugador;
import com.badlogic.gdx.math.Rectangle;
import java.util.ArrayList;

public class ManoManager {

    private final Jugador jugador;
    private final CartaRenderer cartaRenderer;
    private final Viewport viewport;
    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;

    private final float CARTA_ANCHO;
    private final float CARTA_ALTO;
    private final float ESPACIADO;
    private float animatedY;
    private final InputMultiplexer multiplexer;

    // ✅ NUEVO: Lista de inputs (para poder consultar estados)
    private ArrayList<CartaInput> cartaInputs;

    // ✅ NUEVO: Referencia a la zona de juego
    private ZonaJuego zonaJuego;

    public ManoManager(Jugador jugador, CartaRenderer renderer, Viewport viewport,
                       float worldWidth, float worldHeight, float cartaAncho, float cartaAlto) {
        this.jugador = jugador;
        this.cartaRenderer = renderer;
        this.viewport = viewport;
        this.WORLD_WIDTH = worldWidth;
        this.WORLD_HEIGHT = worldHeight;

        this.CARTA_ANCHO = cartaAncho;
        this.CARTA_ALTO = cartaAlto;
        this.ESPACIADO = CARTA_ANCHO * 0.2f;

        this.multiplexer = new InputMultiplexer();
        this.cartaInputs = new ArrayList<>();

        this.animatedY = WORLD_HEIGHT * 0.05f;
    }

    // ✅ NUEVO: Método para vincular la zona de juego
    public void setZonaJuego(ZonaJuego zona) {
        this.zonaJuego = zona;

        // Actualizar todos los inputs existentes
        for (CartaInput input : cartaInputs) {
            input.setZonaJuego(zona);
        }
    }

    public void setPosicionInicialY(float y) {
        this.animatedY = y;
        for (Carta carta : jugador.getMano()) {
            if (carta != null) {
                carta.getLimites().y = y;
            }
        }
    }

    public void inicializarMano() {
        Carta[] mano = jugador.getMano();
        int numCartas = mano.length;

        float anchoTotalMano = (numCartas * CARTA_ANCHO) + ((numCartas - 1) * ESPACIADO);
        float startX = (WORLD_WIDTH - anchoTotalMano) / 2f;
        float Y_FINAL = WORLD_HEIGHT * 0.05f;

        // ✅ Limpiar inputs anteriores
        cartaInputs.clear();

        for (int i = 0; i < numCartas; i++) {
            Carta carta = mano[i];
            if (carta == null) continue;

            float currentX = startX + (i * CARTA_ANCHO) + (i * ESPACIADO);
            carta.updateLimites(currentX, Y_FINAL, CARTA_ANCHO, CARTA_ALTO);

            CartaInput input = new CartaInput(
                    carta,
                    viewport,
                    CARTA_ANCHO,
                    CARTA_ALTO
            );

            // ✅ Vincular la zona de juego si existe
            if (zonaJuego != null) {
                input.setZonaJuego(zonaJuego);
            }

            // ✅ Guardar referencia al input
            cartaInputs.add(input);

            multiplexer.addProcessor(input);
        }
    }

    public void render() {
        for (Carta carta : jugador.getMano()) {
            if (carta == null) continue;

            Rectangle limitesCarta = carta.getLimites();
            float xDraw = limitesCarta.x;
            float yDraw = limitesCarta.y;

            cartaRenderer.render(
                    carta,
                    xDraw,
                    yDraw,
                    limitesCarta.width,
                    limitesCarta.height
            );
        }
    }

    // ✅ NUEVO: Método para saber cuántas cartas se jugaron
    public int getCartasJugadas() {
        int count = 0;
        for (CartaInput input : cartaInputs) {
            if (input.isCartaJugada()) {
                count++;
            }
        }
        return count;
    }

    // ✅ NUEVO: Método para verificar si todas las cartas fueron jugadas
    public boolean todasLasCartasJugadas() {
        return getCartasJugadas() == jugador.getMano().length;
    }

    public InputMultiplexer getInputMultiplexer() {
        return multiplexer;
    }
}