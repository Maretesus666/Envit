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

    public PantallaPartida(Principal game) {

        this.game = game;
        this.partida = new Partida();


    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        fondoPartida= new Texture(Gdx.files.internal("fondos/fondoPartida.png"));
        barajaTexture = new Texture(Gdx.files.internal("sprites/baraja.png"));
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        recortarTresCartas();
        inicializarPosicionesCartas();
    }

    private void recortarTresCartas() {
        int cartaW = barajaTexture.getWidth() / 13;
        int cartaH = barajaTexture.getHeight() / 4;
        for (int i = 0; i < 3; i++) {
            int fila = MathUtils.random(0, 3);
            int col = MathUtils.random(0, 11);
            cartasRecortadas[i] = new TextureRegion(barajaTexture, col * cartaW, fila * cartaH, cartaW, cartaH);
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
            cartaArrastrando[i] = false;
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

        // Dibuja fondo de partida
        batch.begin();
        batch.draw(fondoPartida, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        batch.end();

        // Dibuja fondos de cartas con esquinas redondeadas
        int cartaW = 100, cartaH = 150;
        float radio = 14f;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.WHITE);
        for (int i = 0; i < 3; i++) {
            dibujarRectRedondeado(shapeRenderer, cartaPos[i].x, cartaPos[i].y, cartaW, cartaH, radio);
        }
        shapeRenderer.end();

        // Dibuja las cartas encima del fondo blanco
        batch.begin();
        for (int i = 0; i < 3; i++) {
            batch.draw(cartasRecortadas[i], cartaPos[i].x, cartaPos[i].y, cartaW, cartaH);
        }

        // Overlay de pausa
        if (estado == EstadoJuego.PAUSADO) {
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(getWhitePixel(), 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
            batch.setColor(Color.WHITE);
            // Aquí podrías dibujar el texto de pausa con BitmapFont si lo tienes
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

        // Arrastrar cartas con mouse
        Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        if (Gdx.input.justTouched()) {
            for (int i = 0; i < 3; i++) {
                int cartaW = 100, cartaH = 150;
                if (mouse.x >= cartaPos[i].x && mouse.x <= cartaPos[i].x + cartaW &&
                    mouse.y >= cartaPos[i].y && mouse.y <= cartaPos[i].y + cartaH) {
                    cartaArrastrando[i] = true;
                    cartaSeleccionada = i;
                    arrastreOffset.set(mouse.x - cartaPos[i].x, mouse.y - cartaPos[i].y);
                    break;
                }
            }
        }
        if (Gdx.input.isTouched() && cartaSeleccionada != -1) {
            cartaPos[cartaSeleccionada].set(mouse.x - arrastreOffset.x, mouse.y - arrastreOffset.y);
        }
        if (!Gdx.input.isTouched()) {
            if (cartaSeleccionada != -1) {
                cartaArrastrando[cartaSeleccionada] = false;
                cartaSeleccionada = -1;
            }
        }
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
    }
}