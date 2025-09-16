package com.Envit.Juego.pantallas;

import com.Envit.Juego.Principal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Rectangle;

public class PantallaPartida implements Screen {
    private final Principal game;
    private SpriteBatch batch;
    private Texture barajaTexture;
    private Texture fondoPartida;
    private TextureRegion[][] cartas; // [fila][columna]
    private int[][] cartasMostradas; // [3][2] -> fila, columna
    private Partida partida;
    // Viewport para mantener relación de aspecto
    private static final float VIRTUAL_WIDTH = 1000;
    private static final float VIRTUAL_HEIGHT = 625;
    private Viewport viewport;
    private ShapeRenderer shapeRenderer;

    // Gestión de estados
    private enum EstadoJuego { JUGANDO, PAUSADO, FINALIZADO }
    private EstadoJuego estado = EstadoJuego.JUGANDO;

    // Ahora 6 cartas
    private static final int NUM_CARTAS = 6;
    private TextureRegion[] cartasRecortadas = new TextureRegion[NUM_CARTAS];
    // Posiciones y arrastre
    private Vector2[] cartaPos = new Vector2[NUM_CARTAS];
    private boolean[] cartaArrastrando = new boolean[NUM_CARTAS];
    private Vector2 arrastreOffset = new Vector2();
    private int cartaSeleccionada = -1;

    // Sonido al clickear carta
    private Sound cartaClickSound;

    // Posiciones objetivo para animación
    private Vector2[] cartaPosObjetivo = new Vector2[NUM_CARTAS];

    // Físicas para cartas
    private Vector2[] cartaVel = new Vector2[NUM_CARTAS];
    private Vector2[] cartaAcel = new Vector2[NUM_CARTAS];
    private static final float CARTA_REPELENCIA = 42000f; // fuerza de repulsión entre cartas
    private static final float CARTA_DISTANCIA_MIN = 100f; // distancia mínima antes de repeler

    // Marco de carta
    private Texture marcoCartaTexture;

    // Casilla para jugar cartas
    private Rectangle casillaCartas;

    // Estado de cartas jugadas
    private boolean[] cartaJugada = new boolean[NUM_CARTAS];

    // Textura para la casilla
    private Texture casillaTexture;

    // Para apilar cartas jugadas en la casilla
    private int[] ordenJugadas = new int[NUM_CARTAS];
    private int numCartasJugadas = 0;

    public PantallaPartida(Principal game) {

        this.game = game;
        this.partida = new Partida();


    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        fondoPartida= new Texture(Gdx.files.internal("fondos/fondoPartida.png"));
        barajaTexture = new Texture(Gdx.files.internal("sprites/baraja2.png"));
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        recortarSeisCartas();
        inicializarPosicionesCartas();
        cartaClickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/carta.wav"));
        marcoCartaTexture = new Texture(Gdx.files.internal("sprites/marcoCARTA.png"));
        casillaTexture = new Texture(Gdx.files.internal("sprites/casilla.png"));
        // Casilla centrada en el medio
        float casillaW = 120, casillaH = 170;
        casillaCartas = new Rectangle(
            VIRTUAL_WIDTH/2f - casillaW/2,
            VIRTUAL_HEIGHT/2f - casillaH/2,
            casillaW,
            casillaH
        );
        numCartasJugadas = 0;
        for (int i = 0; i < NUM_CARTAS; i++) {
            cartaJugada[i] = false;
            ordenJugadas[i] = -1;
        }
    }

    // Recorta 6 cartas únicas
    private void recortarSeisCartas() {
        int cartaW = barajaTexture.getWidth() / 11;
        int cartaH = barajaTexture.getHeight() / 4;
        boolean[][] usadas = new boolean[4][11];
        for (int i = 0; i < NUM_CARTAS; i++) {
            TextureRegion region = null;
            int fila, col;
            do {
                fila = MathUtils.random(0, 3);
                col = MathUtils.random(0, 10);
            } while (usadas[fila][col]);
            usadas[fila][col] = true;
            region = new TextureRegion(barajaTexture, col * cartaW, fila * cartaH, cartaW, cartaH);
            cartasRecortadas[i] = region;
        }
    }

    // 3 cartas abajo, 3 arriba
    private void inicializarPosicionesCartas() {
        int cartaW = 100, cartaH = 150, espacio = 20;
        int totalW = 3 * cartaW + 2 * espacio;
        int startX = (int)((VIRTUAL_WIDTH - totalW) / 2);

        int yAbajo = 40;
        int yArriba = (int)(VIRTUAL_HEIGHT - cartaH - 40);

        for (int i = 0; i < 3; i++) {
            cartaPos[i] = new Vector2(startX + i * (cartaW + espacio), yAbajo);
            cartaVel[i] = new Vector2(0, 0);
            cartaAcel[i] = new Vector2(0, 0);
            cartaArrastrando[i] = false;
        }
        for (int i = 3; i < NUM_CARTAS; i++) {
            cartaPos[i] = new Vector2(startX + (i-3) * (cartaW + espacio), yArriba);
            cartaVel[i] = new Vector2(0, 0);
            cartaAcel[i] = new Vector2(0, 0);
            cartaArrastrando[i] = false;
        }
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        Gdx.gl.glClearColor(0, 0.1f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        manejarInput();

        // Físicas entre cartas (solo si no están jugadas)
        for (int i = 0; i < NUM_CARTAS; i++) {
            if (cartaArrastrando[i] || cartaJugada[i]) continue;
            for (int j = 0; j < NUM_CARTAS; j++) {
                if (i == j || cartaArrastrando[j] || cartaJugada[j]) continue;
                float cartaW = 100, cartaH = 150;
                Vector2 centroA = new Vector2(cartaPos[i].x + cartaW/2, cartaPos[i].y + cartaH/2);
                Vector2 centroB = new Vector2(cartaPos[j].x + cartaW/2, cartaPos[j].y + cartaH/2);
                float dist = centroA.dst(centroB);
                if (dist < CARTA_DISTANCIA_MIN) {
                    Vector2 dir = new Vector2(centroA.x - centroB.x, centroA.y - centroB.y);
                    if (dir.len() == 0) dir.set(MathUtils.random(-1f,1f), MathUtils.random(-1f,1f));
                    dir.nor();
                    float fuerza = (CARTA_DISTANCIA_MIN - dist) / CARTA_DISTANCIA_MIN * CARTA_REPELENCIA * delta;
                    cartaPos[i].add(dir.scl(fuerza * 0.01f));
                }
            }
        }

        // Dibuja fondo de partida y casilla
        batch.begin();
        batch.draw(fondoPartida, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.end();

        // Fondo gris semitransparente de la casilla
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 0.45f);
        dibujarRectRedondeado(shapeRenderer, casillaCartas.x, casillaCartas.y, casillaCartas.width, casillaCartas.height, 18f);
        shapeRenderer.end();

        // Dibuja la textura de la casilla
        batch.begin();
        if (casillaTexture != null) {
            batch.draw(casillaTexture, casillaCartas.x, casillaCartas.y, casillaCartas.width, casillaCartas.height);
        }
        batch.end();

        // Dibuja cartas jugadas en la casilla (apiladas, la última arriba)
        batch.begin();
        float cartaW = 100, cartaH = 150;
        float cx = casillaCartas.x + (casillaCartas.width - cartaW)/2;
        float cy = casillaCartas.y + (casillaCartas.height - cartaH)/2;
        for (int k = 0; k < numCartasJugadas; k++) {
            int idx = ordenJugadas[k];
            batch.end();
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 1, 1, 1);
            dibujarRectRedondeado(shapeRenderer, cx, cy, cartaW, cartaH, 14f);
            shapeRenderer.end();
            batch.begin();
            batch.draw(cartasRecortadas[idx], cx, cy, cartaW, cartaH);
            if (marcoCartaTexture != null) {
                batch.draw(marcoCartaTexture, cx, cy, cartaW, cartaH);
            }
        }
        batch.end();

        // Dibuja cartas no jugadas (por arriba de la casilla y apiladas entre sí)
        for (int k = 0; k < NUM_CARTAS; k++) {
            if (cartaJugada[k]) continue;
            float x = cartaPos[k].x, y = cartaPos[k].y;
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 1, 1, 1);
            dibujarRectRedondeado(shapeRenderer, x, y, cartaW, cartaH, 14f);
            shapeRenderer.end();

            batch.begin();
            if (cartasRecortadas[k] != null && cartasRecortadas[k].getRegionWidth() > 0 && cartasRecortadas[k].getRegionHeight() > 0) {
                batch.draw(cartasRecortadas[k], x, y, cartaW, cartaH);
            }
            if (marcoCartaTexture != null) {
                batch.draw(marcoCartaTexture, x, y, cartaW, cartaH);
            }
            batch.end();
        }

        // Overlay de pausa
        batch.begin();
        if (estado == EstadoJuego.PAUSADO) {
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(getWhitePixel(), 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
            batch.setColor(Color.WHITE);
        }
        batch.end();
    }

    // Dibuja un rectángulo con esquinas redondeadas
    private void dibujarRectRedondeado(ShapeRenderer sr, float x, float y, float w, float h, float r) {
        // Centro
        sr.rect(x + r, y + r, w - 2 * r, h - 2 * r);
        // Lados
        sr.rect(x + r, y, w - 2 * r, r); // abajo
        sr.rect(x + r, y + h - r, w - 2 * r, r); // arriba
        sr.rect(x, y + r, r, h - 2 * r); // izq
        sr.rect(x + w - r, y + r, r, h - 2 * r); // der
        // Esquinas
        sr.arc(x + r, y + r, r, 180, 90); // abajo izq
        sr.arc(x + w - r, y + r, r, 270, 90); // abajo der
        sr.arc(x + w - r, y + h - r, r, 0, 90); // arriba der
        sr.arc(x + r, y + h - r, r, 90, 90); // arriba izq
    }

    private void manejarInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (estado == EstadoJuego.JUGANDO) {
                estado = EstadoJuego.PAUSADO;
            } else if (estado == EstadoJuego.PAUSADO) {
                estado = EstadoJuego.JUGANDO;
            }
        }
        if (estado == EstadoJuego.PAUSADO && Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            game.setScreen(new PantallaMenu(game));
        }

        Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        if (Gdx.input.justTouched()) {
            for (int i = 0; i < NUM_CARTAS; i++) {
                if (cartaJugada[i]) continue;
                int cartaW = 100, cartaH = 150;
                if (mouse.x >= cartaPos[i].x && mouse.x <= cartaPos[i].x + cartaW &&
                    mouse.y >= cartaPos[i].y && mouse.y <= cartaPos[i].y + cartaH) {
                    cartaArrastrando[i] = true;
                    cartaSeleccionada = i;
                    arrastreOffset.set(mouse.x - cartaPos[i].x, mouse.y - cartaPos[i].y);
                    if (cartaClickSound != null) cartaClickSound.play(0.7f);
                    break;
                }
            }
        }
        if (Gdx.input.isTouched() && cartaSeleccionada != -1) {
            if (!cartaJugada[cartaSeleccionada]) {
                cartaPos[cartaSeleccionada].set(mouse.x - arrastreOffset.x, mouse.y - arrastreOffset.y);
            }
        }
        if (!Gdx.input.isTouched()) {
            if (cartaSeleccionada != -1) {
                int cartaW = 100, cartaH = 150;
                Rectangle cartaRect = new Rectangle(
                    cartaPos[cartaSeleccionada].x, cartaPos[cartaSeleccionada].y, cartaW, cartaH
                );
                if (cartaRect.overlaps(casillaCartas)) {
                    cartaJugada[cartaSeleccionada] = true;
                    // Apila la carta en la casilla
                    ordenJugadas[numCartasJugadas] = cartaSeleccionada;
                    numCartasJugadas++;
                    cartaPos[cartaSeleccionada].set(
                        casillaCartas.x + (casillaCartas.width - cartaW)/2,
                        casillaCartas.y + (casillaCartas.height - cartaH)/2
                    );
                }
                cartaArrastrando[cartaSeleccionada] = false;
                cartaSeleccionada = -1;
            }
        }
    }

    // Evita que la carta seleccionada se superponga con las otras
    private void ajustarPosicionCarta(int idx) {
        int cartaW = 100, cartaH = 150;
        for (int i = 0; i < NUM_CARTAS; i++) {
            if (i == idx) continue;
            if (rectsSolapan(cartaPos[idx], cartaPos[i], cartaW, cartaH)) {
                // Mueve la carta seleccionada a la posición más cercana libre (a la derecha)
                float espacio = 20;
                float nuevaX = cartaPos[i].x + cartaW + espacio;
                if (nuevaX + cartaW > VIRTUAL_WIDTH) {
                    // Si se sale de pantalla, la mueve a la izquierda
                    nuevaX = cartaPos[i].x - cartaW - espacio;
                    if (nuevaX < 0) nuevaX = 0;
                }
                cartaPos[idx].x = nuevaX;
                // Opcional: también puedes ajustar Y si quieres
            }
        }
    }

    private boolean rectsSolapan(Vector2 a, Vector2 b, int w, int h) {
        return a.x < b.x + w && a.x + w > b.x && a.y < b.y + h && a.y + h > b.y;
    }

    // Textura 1x1 blanca para overlays
    private Texture whitePixel;
    private Texture getWhitePixel() {
        if (whitePixel == null) {
            com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1,1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            whitePixel = new Texture(pixmap);
            pixmap.dispose();
        }
        return whitePixel;
    }

    @Override
    public void resize(int width, int height) {
        if (viewport != null) {
            viewport.update(width, height, true);
        }
    }


    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        barajaTexture.dispose();
        if (whitePixel != null) whitePixel.dispose();
        if(fondoPartida != null) fondoPartida.dispose();
        if (cartaClickSound != null) cartaClickSound.dispose();
        if (marcoCartaTexture != null) marcoCartaTexture.dispose();
        if (casillaTexture != null) casillaTexture.dispose();
    }
}
