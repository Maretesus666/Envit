package juego.pantallas;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import juego.elementos.*;
import juego.elementos.Hud;
import juego.personajes.Jugador;
import juego.personajes.RivalBot;

import java.util.ArrayList;

public class PantallaPartida implements Screen {

    private Game game;
    private Mazo mazo;
    private Partida partida;
    private ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
    private SpriteBatch batch = new SpriteBatch();
    private Texture fondoPartida;
    private Texture mazoSprite;
    private Texture dorsoCartaSprite;
    private boolean inicioRonda = true;
    private Animacion animacion;

    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private CartaRenderer cartaRenderer;
    private ManoManager manoManager;

    private RivalBot rivalBot;
    private ManoRivalRenderer manoRivalRenderer;
    private ZonaJuego zonaJuegoRival;
    private ZonaJuego zonaJuegoJugador;

    private Hud hud;
    private BitmapFont font;

    private final float WORLD_WIDTH = 640;
    private final float WORLD_HEIGHT = 480;

    private final float CARTA_PROPORCION_ANCHO = 0.1f;
    private final float CARTA_RELACION_ASPECTO = 1.4f;

    private final float CARTA_ANCHO = WORLD_WIDTH * CARTA_PROPORCION_ANCHO;
    private final float CARTA_ALTO = CARTA_ANCHO * CARTA_RELACION_ASPECTO;
    private int mano;


    private boolean mostrarPantallaFinal = false;
    private float tiempoEnPantallaFinal = 0f;
    private final float TIEMPO_PANTALLA_FINAL = 5.0f;


    private com.badlogic.gdx.math.Rectangle btnTrucoRect;
    private boolean btnTrucoHovered = false;
    private float animacionTrucoPulso = 0f;

    public PantallaPartida(Game game) {
        this.game = game;
        this.crearJugadores();
    }

    private void crearJugadores() {
        Jugador jugador = new Jugador("Tú");
        Jugador rival = new Jugador("Rival");

        jugadores.add(jugador);
        jugadores.add(rival);
    }

    @Override
    public void show() {
        fondoPartida = new Texture(Gdx.files.internal("fondos/fondoPartida.png"));
        mazoSprite = new Texture(Gdx.files.internal("sprites/mazo_sprite.png"));
        dorsoCartaSprite = new Texture(Gdx.files.internal("sprites/dorso.png"));
        mazo = new Mazo();
        partida = new Partida();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        shapeRenderer = new ShapeRenderer();
        cartaRenderer = new CartaRenderer(batch, shapeRenderer, viewport);

        font = new BitmapFont();
        font.getData().setScale(1.2f);
        hud = new Hud(font, jugadores.get(0), jugadores.get(1), WORLD_WIDTH, WORLD_HEIGHT);

        // Crear las dos zonas de juego
        float zonaAncho = CARTA_ANCHO * 1.5f;
        float zonaAlto = CARTA_ALTO * 1.3f;

        float zonaJugadorX = (WORLD_WIDTH - zonaAncho) / 2f;
        float zonaJugadorY = (WORLD_HEIGHT / 2f) - zonaAlto;
        zonaJuegoJugador = new ZonaJuego(zonaJugadorX, zonaJugadorY, zonaAncho, zonaAlto);
        zonaJuegoJugador.setCartaRenderer(cartaRenderer);

        float zonaRivalX = (WORLD_WIDTH - zonaAncho) / 2f;
        float zonaRivalY = (WORLD_HEIGHT / 2f) + 20;
        zonaJuegoRival = new ZonaJuego(zonaRivalX, zonaRivalY, zonaAncho, zonaAlto);
        zonaJuegoRival.setCartaRenderer(cartaRenderer);

        manoManager = new ManoManager(
                jugadores.get(0),
                cartaRenderer,
                viewport,
                WORLD_WIDTH,
                WORLD_HEIGHT,
                CARTA_ANCHO,
                CARTA_ALTO
        );
        manoManager.setZonaJuego(zonaJuegoJugador);

        rivalBot = new RivalBot(jugadores.get(1), zonaJuegoRival);
        rivalBot.setDelay(1.5f);
        rivalBot.setProbabilidadTruco(0.3f);

        manoRivalRenderer = new ManoRivalRenderer(
                jugadores.get(1),
                cartaRenderer,
                dorsoCartaSprite,
                zonaJuegoRival,
                WORLD_WIDTH,
                WORLD_HEIGHT,
                CARTA_ANCHO,
                CARTA_ALTO
        );

        animacion = new Animacion(
                WORLD_WIDTH,
                WORLD_HEIGHT,
                CARTA_ANCHO,
                CARTA_ALTO,
                dorsoCartaSprite,
                manoManager
        );

        partida.inicializar(zonaJuegoJugador, zonaJuegoRival, rivalBot,
                jugadores.get(0), jugadores.get(1), mano);


        rivalBot.setPartida(partida);

        Gdx.input.setInputProcessor(manoManager.getInputMultiplexer());


        float btnTrucoAncho = 80f;
        float btnTrucoAlto = 60f;
        float margenIzq = 20f;
        float btnTrucoX = margenIzq;
        float btnTrucoY = (WORLD_HEIGHT - btnTrucoAlto) / 2f;
        btnTrucoRect = new com.badlogic.gdx.math.Rectangle(
                btnTrucoX, btnTrucoY, btnTrucoAncho, btnTrucoAlto
        );

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0.1f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // ✅ NUEVO: Si la partida terminó, mostrar pantalla de ganador
        if (mostrarPantallaFinal) {
            renderPantallaFinal(delta);
            return;
        }

        // 1. DIBUJAR FONDO Y MAZO
        this.batch.setProjectionMatrix(viewport.getCamera().combined);
        this.batch.begin();
        batch.draw(fondoPartida, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        float mazoAncho = CARTA_ANCHO;
        float mazoAlto = CARTA_ALTO;
        float margenHorizontal = WORLD_WIDTH * 0.05f;
        float mazoX = WORLD_WIDTH - mazoAncho - margenHorizontal;
        float mazoY = (WORLD_HEIGHT - mazoAlto) / 2f;

        animacion.render(batch);
        batch.draw(mazoSprite, mazoX, mazoY, mazoAncho, mazoAlto);

        this.batch.end();

        // 2. DIBUJAR LOS FONDOS DE LAS ZONAS DE JUEGO
        zonaJuegoJugador.renderFondo(shapeRenderer);
        zonaJuegoRival.renderFondo(shapeRenderer);

        // 3. DIBUJAR LAS CARTAS EN MANO
        this.batch.setProjectionMatrix(viewport.getCamera().combined);

        this.batch.begin();
        manoRivalRenderer.render(batch);
        this.batch.end();

        manoManager.render();

        // 4. DIBUJAR LAS CARTAS DENTRO DE LAS ZONAS (jugadas)
        zonaJuegoJugador.renderCartas();
        zonaJuegoRival.renderCartas();


        this.batch.setProjectionMatrix(viewport.getCamera().combined);
        renderBotonTruco(delta);

        this.batch.setProjectionMatrix(viewport.getCamera().combined);
        hud.render(batch, partida.getManoActual(), partida.esTurnoJugador(),
                partida.isTrucoActivoEnManoActual(), partida.getManoTrucoUsada());
    }

    public void update(float delta) {
        animacion.update(delta);
        partida.update(delta);

        animacionTrucoPulso += delta * 3f;
        if (animacionTrucoPulso > Math.PI * 2) {
            animacionTrucoPulso = 0f;
        }


        if (!mostrarPantallaFinal && !partida.partidaTerminada()) {
            detectarInputTruco();
        }

        if (partida.partidaTerminada() && !mostrarPantallaFinal) {
            mostrarPantallaFinal = true;
            tiempoEnPantallaFinal = 0f;

            // Desactivar input del jugador
            Gdx.input.setInputProcessor(null);

            System.out.println("¡PARTIDA TERMINADA! Ganador: " + partida.getGanador().getNombre());
            return;
        }

        // Si la partida está lista para nueva ronda (pero NO terminada)
        if (partida.rondaTerminada()) {
            inicioRonda = true;
            partida.nuevaRonda();
        }

        if (inicioRonda) {
            zonaJuegoJugador.limpiar();
            zonaJuegoRival.limpiar();
            jugadores.get(0).limpiarMazo();
            jugadores.get(1).limpiarMazo();

            partida.repartirCartas(jugadores.get(0), jugadores.get(1));

            manoManager.inicializarMano();
            manoRivalRenderer.inicializarPosiciones();
            animacion.iniciarAnimacionReparto();

            inicioRonda = false;
        }
    }


    private void renderPantallaFinal(float delta) {
        tiempoEnPantallaFinal += delta;

        // Fondo oscuro semi-transparente
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.8f);
        shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Mostrar mensaje de ganador
        batch.setProjectionMatrix(viewport.getCamera().combined);

        Jugador ganador = partida.getGanador();
        boolean ganoJugador = ganador == jugadores.get(0);

        String mensaje = ganoJugador ? "¡VICTORIA!" : "DERROTA";
        Color colorMensaje = ganoJugador ? new Color(0.2f, 0.9f, 0.2f, 1f) : new Color(0.9f, 0.2f, 0.2f, 1f);

        hud.dibujarMensajeCentral(batch, mensaje, colorMensaje);

        // Mostrar puntuación final
        batch.begin();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);

        String puntuacion = jugadores.get(0).getNombre() + ": " + jugadores.get(0).getPuntos() +
                " - " + jugadores.get(1).getNombre() + ": " + jugadores.get(1).getPuntos();

        com.badlogic.gdx.graphics.g2d.GlyphLayout layout =
                new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, puntuacion);

        float x = (WORLD_WIDTH - layout.width) / 2f;
        float y = WORLD_HEIGHT / 2f - 50f;

        font.draw(batch, puntuacion, x, y);

        // Mensaje de retorno al menú
        font.getData().setScale(1.0f);
        String mensajeVolver = "Volviendo al menú en " + (int)(TIEMPO_PANTALLA_FINAL - tiempoEnPantallaFinal + 1) + "...";

        if (tiempoEnPantallaFinal >= TIEMPO_PANTALLA_FINAL) {
            mensajeVolver = "Presiona cualquier tecla para continuar";

            // Si pasa el tiempo o toca, volver al menú
            if (Gdx.input.isTouched() || Gdx.input.isKeyJustPressed(com.badlogic.gdx.Input.Keys.ANY_KEY)) {
                volverAlMenu();
            }
        }

        layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, mensajeVolver);
        x = (WORLD_WIDTH - layout.width) / 2f;
        y = 50f;

        font.draw(batch, mensajeVolver, x, y);

        batch.end();

        // Auto-retorno después del tiempo límite
        if (tiempoEnPantallaFinal >= TIEMPO_PANTALLA_FINAL + 10f) {
            volverAlMenu();
        }
    }


    private void volverAlMenu() {
        game.setScreen(new PantallaMenu((juego.Principal) game));
        dispose();
    }


    private void renderBotonTruco(float delta) {
        boolean trucoDisponible = !partida.isTrucoUsado();

        // Verificar si el mouse está sobre el botón
        com.badlogic.gdx.math.Vector2 mouse = viewport.unproject(
                new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY())
        );
        btnTrucoHovered = btnTrucoRect.contains(mouse.x, mouse.y) && trucoDisponible;

        // Calcular color y escala según estado
        Color colorBtn;
        float escala = 1.0f;

        if (!trucoDisponible) {
            // Deshabilitado (gris)
            colorBtn = new Color(0.3f, 0.3f, 0.3f, 0.5f);
        } else if (btnTrucoHovered) {
            // Hover (rojo brillante pulsante)
            float pulso = (float)Math.sin(animacionTrucoPulso) * 0.1f + 0.9f;
            colorBtn = new Color(1.0f * pulso, 0.2f, 0.2f, 1f);
            escala = 1.1f;
        } else {
            // Normal (rojo)
            colorBtn = new Color(0.9f, 0.1f, 0.1f, 0.9f);
        }

        // Dibujar fondo del botón
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(colorBtn);

        float offsetX = (btnTrucoRect.width * escala - btnTrucoRect.width) / 2f;
        float offsetY = (btnTrucoRect.height * escala - btnTrucoRect.height) / 2f;

        shapeRenderer.rect(
                btnTrucoRect.x - offsetX,
                btnTrucoRect.y - offsetY,
                btnTrucoRect.width * escala,
                btnTrucoRect.height * escala
        );
        shapeRenderer.end();

        // Borde del botón
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(
                btnTrucoRect.x - offsetX,
                btnTrucoRect.y - offsetY,
                btnTrucoRect.width * escala,
                btnTrucoRect.height * escala
        );
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Dibujar texto
        batch.begin();
        font.setColor(trucoDisponible ? Color.WHITE : new Color(0.5f, 0.5f, 0.5f, 1f));
        font.getData().setScale(1.8f);

        String textoTruco = "TRUCO";
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout =
                new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, textoTruco);

        float textX = btnTrucoRect.x + (btnTrucoRect.width - layout.width) / 2f;
        float textY = btnTrucoRect.y + (btnTrucoRect.height + layout.height) / 2f;

        font.draw(batch, textoTruco, textX, textY);

        // Si está activo en esta mano, mostrar indicador
        if (partida.isTrucoActivoEnManoActual()) {
            font.getData().setScale(0.8f);
            font.setColor(Color.YELLOW);
            String textoActivo = "x2";
            layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, textoActivo);
            float x2X = btnTrucoRect.x + (btnTrucoRect.width - layout.width) / 2f;
            float x2Y = btnTrucoRect.y - 5f;
            font.draw(batch, textoActivo, x2X, x2Y);
        }

        batch.end();
    }


    private void detectarInputTruco() {
        if (Gdx.input.justTouched()) {
            com.badlogic.gdx.math.Vector2 touch = viewport.unproject(
                    new com.badlogic.gdx.math.Vector2(Gdx.input.getX(), Gdx.input.getY())
            );

            if (btnTrucoRect.contains(touch.x, touch.y)) {
                // Intentar cantar truco
                boolean exito = partida.cantarTruco(Partida.TipoJugador.JUGADOR_1);

                if (exito) {
                    System.out.println("¡TRUCO cantado por el jugador!");
                    // TODO: Agregar sonido de truco aquí
                } else {
                    System.out.println("El truco ya fue usado en esta ronda");
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("Se ha redimensionado la pantalla a: " + width + "x" + height);
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (fondoPartida != null) fondoPartida.dispose();
        if (mazoSprite != null) mazoSprite.dispose();
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
        if (font != null) font.dispose();
    }
}