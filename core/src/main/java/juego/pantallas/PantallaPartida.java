package juego.pantallas;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import jdk.internal.org.jline.terminal.TerminalBuilder;
import juego.elementos.Mazo;
import juego.elementos.CartaRenderer;
import juego.personajes.Jugador;
import juego.elementos.CartaInput;
import java.util.ArrayList;
import com.badlogic.gdx.InputMultiplexer;

public class PantallaPartida implements Screen {

    private Game game;
    private Mazo mazo;
    private Partida partida;
    private ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
    private SpriteBatch batch = new SpriteBatch();
    private Texture fondoPartida;
    private boolean inicioRonda = true;
    private float cartaX, cartaY, cartaAncho, cartaAlto;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private FitViewport viewport;
    private CartaRenderer cartaRenderer;
    private CartaInput cartaInput;
    private final float WORLD_WIDTH = 640;
    private final float WORLD_HEIGHT = 480;
    private final float CARTA_ANCHO_INICIAL = WORLD_WIDTH * 0.1f;
    private final float CARTA_ALTO_INICIAL = CARTA_ANCHO_INICIAL * 1.4f;

    public PantallaPartida(Game game) {
        this.game = game;
        this.crearJugadores();
    }


    private void crearJugadores() {
        jugadores.add(new Jugador());
        jugadores.add(new Jugador());
    }

    @Override
    public void show() {
        fondoPartida = new Texture(Gdx.files.internal("fondos/fondoPartida.png"));
        mazo = new Mazo();
        partida = new Partida();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        shapeRenderer = new ShapeRenderer();
        cartaRenderer = new CartaRenderer(batch, shapeRenderer, viewport);
        // 1. Calcular la posición inicial de la carta
        float initialX = (WORLD_WIDTH - CARTA_ANCHO_INICIAL) / 2f;
        float initialY = (WORLD_HEIGHT - CARTA_ALTO_INICIAL) / 2f;
        // Asignar la posición y el tamaño INICIAL al Rectangle de la carta
        this.mazo.getCarta(0).updateLimites(initialX, initialY, CARTA_ANCHO_INICIAL, CARTA_ALTO_INICIAL);
        // 2. Inicializar el procesador de entrada de la carta (para arrastre)
        cartaInput = new CartaInput(
                this.mazo.getCarta(0),
                viewport,
                CARTA_ANCHO_INICIAL,
                CARTA_ALTO_INICIAL
        );
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(cartaInput);
        Gdx.input.setInputProcessor(multiplexer);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        update();

        Gdx.gl.glClearColor(0, 0.1f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Rectangle limitesCarta = this.mazo.getCarta(0).getLimites();
        // 1. DIBUJAR FONDO DEL TABLERO
        this.batch.setProjectionMatrix(viewport.getCamera().combined);
        this.batch.begin();
        batch.draw(fondoPartida, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        this.batch.end();

        // 2. DIBUJAR LA CARTA (DELEGADO AL RENDERER)
        this.batch.setProjectionMatrix(viewport.getCamera().combined);

        cartaRenderer.render(
                this.jugadores.get(0).getMano()[0],
                limitesCarta.x,
                limitesCarta.y,
                limitesCarta.width,
                limitesCarta.height
        );

    }

    public void update(){
        if(this.inicioRonda){
            this.partida.repartirCartas(jugadores.get(0), jugadores.get(1));
            this.inicioRonda = false;
            System.out.println("Carta:" + this.jugadores.get(0).getMano()[0].getNombre());
        }
    }


    @Override
    public void resize(int width, int height) {
        System.out.println("Se ha redimensionado la pantalla a: " + width + "x" + height);
        viewport.update(width, height, true);
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();
        cartaAncho = worldWidth * 0.1f;
        cartaAlto = cartaAncho * 1.4f;
        cartaX = (worldWidth - cartaAncho) / 2f;
        cartaY = (worldHeight - cartaAlto) / 2f;
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
        if (batch != null) batch.dispose();
        if (shapeRenderer != null) shapeRenderer.dispose();
    }
}