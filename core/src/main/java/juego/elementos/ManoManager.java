package juego.elementos;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.viewport.Viewport;
import juego.personajes.Jugador;
import com.badlogic.gdx.math.Rectangle;

public class ManoManager {

    private final Jugador jugador;
    private final CartaRenderer cartaRenderer;
    private final Viewport viewport;
    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;

    private final float CARTA_ANCHO;
    private final float CARTA_ALTO;
    private final float ESPACIADO;
    private float animatedY; // Posición Y controlada por la animación
    private final InputMultiplexer multiplexer;

    public ManoManager(Jugador jugador, CartaRenderer renderer, Viewport viewport, float worldWidth, float worldHeight, float cartaAncho, float cartaAlto) {
        this.jugador = jugador;
        this.cartaRenderer = renderer;
        this.viewport = viewport;
        this.WORLD_WIDTH = worldWidth;
        this.WORLD_HEIGHT = worldHeight;

        // Se usan los valores pasados desde PantallaPartida
        this.CARTA_ANCHO = cartaAncho;
        this.CARTA_ALTO = cartaAlto;
        this.ESPACIADO = CARTA_ANCHO * 0.2f;

        this.multiplexer = new InputMultiplexer();

        // Posición Y final por defecto (por si la animación se salta o termina)
        this.animatedY = WORLD_HEIGHT * 0.05f;
    }

    // Método llamado por la clase Animacion para establecer la coordenada Y
    public void setPosicionInicialY(float y) {
        this.animatedY = y;
        for (Carta carta : jugador.getMano()) {

            carta.getLimites().y = y;
        }
    }

    public void inicializarMano() {

        Carta[] mano = jugador.getMano();
        int numCartas = mano.length;

        float anchoTotalMano = (numCartas * CARTA_ANCHO) + ((numCartas - 1) * ESPACIADO);
        float startX = (WORLD_WIDTH - anchoTotalMano) / 2f;

        // Coordenada Y final de la mano (para límites)
        float Y_FINAL = WORLD_HEIGHT * 0.05f;

        for (int i = 0; i < numCartas; i++) {
            Carta carta = mano[i];

            float currentX = startX + (i * CARTA_ANCHO) + (i * ESPACIADO);

            // Los límites se establecen en la posición final.
            // La animación moverá la posición de dibujo temporalmente.
            carta.updateLimites(currentX, Y_FINAL, CARTA_ANCHO, CARTA_ALTO);

            CartaInput input = new CartaInput(
                    carta,
                    viewport,
                    CARTA_ANCHO,
                    CARTA_ALTO
            );

            multiplexer.addProcessor(input);
        }
    }

    public void render() {
        for (Carta carta : jugador.getMano()) {

            Rectangle limitesCarta = carta.getLimites();

            // La posición X siempre viene de los límites estáticos
            float xDraw = limitesCarta.x;

            // La posición Y viene de animatedY (controlada por la animación)
            // Si la animación terminó, animatedY será la posición final (Y_FINAL).
            float yDraw = limitesCarta.y;

            // Nota: Se asume que tu lógica de arrastre (CartaInput) actualiza
            // directamente limitesCarta.x e limitesCarta.y cuando la carta se mueve,
            // por lo que este render usará las coordenadas de arrastre automáticamente.

            cartaRenderer.render(
                    carta,
                    xDraw,
                    yDraw, // Usamos la coordenada Y animada/final
                    limitesCarta.width,
                    limitesCarta.height
            );
        }
    }

    public InputMultiplexer getInputMultiplexer() {
        return multiplexer;
    }
}