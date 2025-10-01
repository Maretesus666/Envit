package juego.elementos;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.math.Rectangle; // Necesario para obtener los límites de la carta

public class CartaInput implements InputProcessor {

    private final Carta cartaParaMover;
    private final Viewport viewport;
    private final float cartaAncho;
    private final float cartaAlto;

    private boolean isDragging = false;
    private final Vector3 touchPoint = new Vector3();

    // Offset (desplazamiento) para un arrastre suave
    private float dragOffsetX;
    private float dragOffsetY;

    // Constructor: necesita la carta, el área de vista y el tamaño para funcionar
    public CartaInput(Carta carta, Viewport viewport, float ancho, float alto) {
        this.cartaParaMover = carta;
        this.viewport = viewport;
        this.cartaAncho = ancho;
        this.cartaAlto = alto;
    }

    // Método que se llama al presionar el mouse o la pantalla
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // 1. Convertir coordenadas de pantalla (píxeles) a coordenadas de mundo
        touchPoint.set(screenX, screenY, 0);
        viewport.unproject(touchPoint);

        // 2. Comprobar si las coordenadas están dentro del límite de la carta
        Rectangle limitesCarta = cartaParaMover.getLimites();

        if (limitesCarta.contains(touchPoint.x, touchPoint.y)) {
            isDragging = true;

            // 3. Calcular el offset para que la carta no salte al centro del cursor
            dragOffsetX = touchPoint.x - limitesCarta.x;
            dragOffsetY = touchPoint.y - limitesCarta.y;

            return true; // Indicamos que hemos manejado el evento
        }
        return false; // No se hizo clic en la carta
    }

    // Método que se llama mientras se arrastra el mouse/dedo
    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (isDragging) {
            // 1. Convertir coordenadas a mundo
            touchPoint.set(screenX, screenY, 0);
            viewport.unproject(touchPoint);

            // 2. Calcular la nueva posición de la carta (posición del cursor - offset)
            float newX = touchPoint.x - dragOffsetX;
            float newY = touchPoint.y - dragOffsetY;

            // 3. ¡ACTUALIZAR la posición real de la carta!
            // Esto es crucial para que render() la dibuje en el nuevo lugar
            cartaParaMover.updateLimites(newX, newY, cartaAncho, cartaAlto);

            return true;
        }
        return false;
    }

    // Método que se llama al soltar el mouse/dedo
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (isDragging) {
            isDragging = false;
            // Aquí iría la lógica de "soltar":
            // - Comprobar si se soltó en la zona de juego
            // - Devolver la carta a la mano si no es una posición válida
            return true;
        }
        return false;
    }
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        // Este método se usa en plataformas como iOS o Android cuando el SO
        // interrumpe un toque/arrastre (ej: notificación entrante o cambio de app).
        // Si la carta estaba siendo arrastrada, deberías restablecer su estado aquí.
        if (isDragging) {
            isDragging = false;
            // Opcionalmente, aquí podrías devolver la carta a su posición inicial segura
            return true;
        }
        return false;
    }
    // Métodos obligatorios de la interfaz InputProcessor (no usados por el arrastre)
    @Override public boolean mouseMoved(int screenX, int screenY) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
}
