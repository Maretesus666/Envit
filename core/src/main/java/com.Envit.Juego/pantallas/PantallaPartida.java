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

    // Solo 3 cartas
    private TextureRegion[] cartasRecortadas = new TextureRegion[3];
    // Posiciones y arrastre
    private Vector2[] cartaPos = new Vector2[3];
    private boolean[] cartaArrastrando = new boolean[3];
    private Vector2 arrastreOffset = new Vector2();
    private int cartaSeleccionada = -1;

    // Sonido al clickear carta
    private Sound cartaClickSound;

    // Posiciones objetivo para animación
    private Vector2[] cartaPosObjetivo = new Vector2[3];

    // Físicas para cartas
    private Vector2[] cartaVel = new Vector2[3];
    private Vector2[] cartaAcel = new Vector2[3];
    private static final float CARTA_FUERZA_ARRASTRE = 200f;
    private static final float CARTA_FRICCION = 0.85f;
    private static final float CARTA_LERP = 0.18f; // velocidad de interpolación
    private static final float CARTA_REPELENCIA = 1200f; // fuerza de repulsión entre cartas
    private static final float CARTA_DISTANCIA_MIN = 90f; // distancia mínima antes de repeler

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
        recortarTresCartas();
        inicializarPosicionesCartas();
        cartaClickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/carta.wav"));
    }

    private void recortarTresCartas() {
        int cartaW = barajaTexture.getWidth() / 11;
        int cartaH = barajaTexture.getHeight() / 4;
        for (int i = 0; i < 3; i++) {
            TextureRegion region = null;
            do {
                int fila = MathUtils.random(0, 3);
                int col = MathUtils.random(0, 10); // 11 columnas, índice 0-10
                region = new TextureRegion(barajaTexture, col * cartaW, fila * cartaH, cartaW, cartaH);
            } while (region == null || region.getRegionWidth() == 0 || region.getRegionHeight() == 0);
            cartasRecortadas[i] = region;
        }
    }

    private void inicializarPosicionesCartas() {
        int cartaW = 100, cartaH = 150;
        int espacio = 20;
        int totalW = 3 * cartaW + 2 * espacio;
        int startX = (int)((VIRTUAL_WIDTH - totalW) / 2);
        int y = (int)(VIRTUAL_HEIGHT / 2 - cartaH / 2);
        for (int i = 0; i < 3; i++) {
            cartaPos[i] = new Vector2(startX + i * (cartaW + espacio), y);
            cartaPosObjetivo[i] = new Vector2(cartaPos[i].x, cartaPos[i].y);
            cartaArrastrando[i] = false;
            cartaVel[i] = new Vector2(0, 0);
            cartaAcel[i] = new Vector2(0, 0);
        }
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);

        Gdx.gl.glClearColor(0, 0.5f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        manejarInput();

        // Físicas y animación de cartas
        for (int i = 0; i < 3; i++) {
            // Repulsión entre cartas
            for (int j = 0; j < 3; j++) {
                if (i == j) continue;
                float cartaW = 100, cartaH = 150;
                Vector2 centroA = new Vector2(cartaPos[i].x + cartaW/2, cartaPos[i].y + cartaH/2);
                Vector2 centroB = new Vector2(cartaPos[j].x + cartaW/2, cartaPos[j].y + cartaH/2);
                float dist = centroA.dst(centroB);
                if (dist < CARTA_DISTANCIA_MIN) {
                    // Calcula fuerza de repulsión
                    Vector2 dir = new Vector2(centroA.x - centroB.x, centroA.y - centroB.y);
                    if (dir.len() == 0) dir.set(MathUtils.random(-1f,1f), MathUtils.random(-1f,1f));
                    dir.nor();
                    float fuerza = (CARTA_DISTANCIA_MIN - dist) / CARTA_DISTANCIA_MIN * CARTA_REPELENCIA * delta;
                    cartaVel[i].add(dir.scl(fuerza));
                }
            }

            if (cartaArrastrando[i]) {
                // Mientras se arrastra, la carta sigue al mouse con aceleración
                Vector2 target = cartaPosObjetivo[i];
                Vector2 dir = new Vector2(target.x - cartaPos[i].x, target.y - cartaPos[i].y);
                cartaAcel[i].set(dir.scl(CARTA_FUERZA_ARRASTRE));
            } else {
                // Cuando no se arrastra, desacelera por fricción
                cartaAcel[i].setZero();
                cartaVel[i].scl(CARTA_FRICCION);
            }
            cartaVel[i].add(cartaAcel[i].scl(delta));
            cartaPos[i].add(cartaVel[i].scl(delta));
            // Si está muy cerca del objetivo y no arrastrando, lo ajusta
            if (!cartaArrastrando[i] && cartaPos[i].dst(cartaPosObjetivo[i]) > 1f) {
                cartaPos[i].lerp(cartaPosObjetivo[i], CARTA_LERP);
            }
        }

        // Dibuja fondo de partida
        batch.begin();
        batch.draw(fondoPartida, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.end();

        // Dibuja fondos de cartas con esquinas redondeadas (opaco)
        int cartaW = 100, cartaH = 150;
        float radio = 14f;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 1, 1, 1); // Blanco opaco
        int[] orden = {0, 1, 2};
        if (cartaSeleccionada != -1) {
            int idx = cartaSeleccionada;
            if (orden[2] != idx) {
                for (int i = 0; i < 3; i++) {
                    if (orden[i] == idx) {
                        for (int j = i; j < 2; j++) orden[j] = orden[j+1];
                        orden[2] = idx;
                        break;
                    }
                }
            }
        }
        for (int k = 0; k < 3; k++) {
            int i = orden[k];
            dibujarRectRedondeado(shapeRenderer, cartaPos[i].x, cartaPos[i].y, cartaW, cartaH, radio);
        }
        shapeRenderer.end();

        // Dibuja las cartas encima del fondo blanco, en el mismo orden
        batch.begin();
        for (int k = 0; k < 3; k++) {
            int i = orden[k];
            if (cartasRecortadas[i] != null && cartasRecortadas[i].getRegionWidth() > 0 && cartasRecortadas[i].getRegionHeight() > 0) {
                batch.draw(cartasRecortadas[i], cartaPos[i].x, cartaPos[i].y, cartaW, cartaH);
            }
        }
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
            for (int i = 0; i < 3; i++) {
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
            cartaPosObjetivo[cartaSeleccionada].set(mouse.x - arrastreOffset.x, mouse.y - arrastreOffset.y);
        }
        if (!Gdx.input.isTouched()) {
            if (cartaSeleccionada != -1) {
                cartaArrastrando[cartaSeleccionada] = false;
                ajustarPosicionCarta(cartaSeleccionada);
                cartaSeleccionada = -1;
            }
        }
    }

    // Evita que la carta seleccionada se superponga con las otras
    private void ajustarPosicionCarta(int idx) {
        int cartaW = 100, cartaH = 150;
        for (int i = 0; i < 3; i++) {
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
    }
}

