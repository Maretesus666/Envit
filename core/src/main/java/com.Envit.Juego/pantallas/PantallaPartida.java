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

    // Gestión de estados
    private enum EstadoJuego { JUGANDO, PAUSADO, FINALIZADO }
    private EstadoJuego estado = EstadoJuego.JUGANDO;

    public PantallaPartida(Principal game) {

        this.game = game;
        this.partida = new Partida();


    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        fondoPartida= new Texture(Gdx.files.internal("fondos/fondoPartida.png"));
        barajaTexture = new Texture(Gdx.files.internal("sprites/baraja.png"));
        cartas = new TextureRegion[4][12];
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        recortarCartas();
        seleccionarCartasAleatorias();
    }

    private void recortarCartas() {
        int cartaW = barajaTexture.getWidth() / 12;
        int cartaH = barajaTexture.getHeight() / 4;
        for (int fila = 0; fila < 4; fila++) {
            for (int col = 0; col < 12; col++) {
                cartas[fila][col] = new TextureRegion(barajaTexture, col * cartaW, fila * cartaH, cartaW, cartaH);
            }
        }
    }

    private void seleccionarCartasAleatorias() {
        cartasMostradas = new int[3][2];
        for (int i = 0; i < 3; i++) {
            int fila = MathUtils.random(0, 3);
            int col = MathUtils.random(0, 11);
            cartasMostradas[i][0] = fila;
            cartasMostradas[i][1] = col;
        }
    }

    @Override
    public void render(float delta) {
        // Aplicar viewport antes de dibujar
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        Gdx.gl.glClearColor(0, 0.5f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        manejarInput();

        batch.begin();
        batch.draw(fondoPartida, 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
        int cartaW = 100, cartaH = 150;
        int espacio = 40;
        int totalW = 3 * cartaW + 2 * espacio;
        int startX = (int)((VIRTUAL_WIDTH - totalW) / 2);
        int y = (int)(VIRTUAL_HEIGHT / 2 - cartaH / 2);

        // Dibuja cartas solo si no está finalizado
        if (estado != EstadoJuego.FINALIZADO) {
            for (int i = 0; i < 3; i++) {
                int fila = cartasMostradas[i][0];
                int col = cartasMostradas[i][1];
                TextureRegion carta = cartas[fila][col];
                batch.draw(carta, startX + i * (cartaW + espacio), y, cartaW, cartaH);
            }
        }

        // Overlay de pausa
        if (estado == EstadoJuego.PAUSADO) {
            // Fondo semitransparente
            batch.setColor(0, 0, 0, 0.5f);
            batch.draw(getWhitePixel(), 0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
            batch.setColor(Color.WHITE);
            // Texto de pausa
            // Puedes usar BitmapFont si tienes una fuente cargada globalmente
            // Aquí solo se muestra el concepto
            // font.draw(batch, "PAUSA\n[ESC] Reanudar\n[M] Menú", VIRTUAL_WIDTH/2-100, VIRTUAL_HEIGHT/2+40);
        }
        batch.end();
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
        barajaTexture.dispose();
        if (whitePixel != null) whitePixel.dispose();
        if(fondoPartida != null) fondoPartida.dispose();
    }
}