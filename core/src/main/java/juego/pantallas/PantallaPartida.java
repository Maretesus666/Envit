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


import juego.elementos.Mazo;
import juego.elementos.CartaRenderer;
import juego.personajes.Jugador;
import juego.elementos.ManoManager;
import juego.elementos.Animacion;


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

    private final float WORLD_WIDTH = 640;
    private final float WORLD_HEIGHT = 480;

    // ✅ CONSTANTES DE TAMAÑO DE CARTA
    private final float CARTA_PROPORCION_ANCHO = 0.1f;
    private final float CARTA_RELACION_ASPECTO = 1.4f;

    private final float CARTA_ANCHO = WORLD_WIDTH * CARTA_PROPORCION_ANCHO;
    private final float CARTA_ALTO = CARTA_ANCHO * CARTA_RELACION_ASPECTO;

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
        mazoSprite = new Texture(Gdx.files.internal("sprites/mazo_sprite.png"));
        dorsoCartaSprite = new Texture(Gdx.files.internal("sprites/dorso.png"));
        mazo = new Mazo();
        partida = new Partida();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        shapeRenderer = new ShapeRenderer();
        cartaRenderer = new CartaRenderer(batch, shapeRenderer, viewport);

        // 1. REPARTIR CARTAS


        // 2. INICIALIZAR EL MANAGER DE LA MANO (Pasando el tamaño de la carta)
        manoManager = new ManoManager(
                jugadores.get(0),
                cartaRenderer,
                viewport,
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

        // 3. Posicionar todas las cartas y configurar el arrastre

        // 4. Asignar el InputMultiplexer del Manager
        Gdx.input.setInputProcessor(manoManager.getInputMultiplexer());

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0.1f, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 1. DIBUJAR FONDO Y MAZO
        this.batch.setProjectionMatrix(viewport.getCamera().combined);
        this.batch.begin();
        batch.draw(fondoPartida, 0, 0, WORLD_WIDTH, WORLD_HEIGHT);

        // --- DIBUJAR EL MAZO USANDO LAS CONSTANTES DE LA CARTA ---

        // El mazo tiene el mismo tamaño que una carta
        float mazoAncho = CARTA_ANCHO;
        float mazoAlto = CARTA_ALTO;

        // Posición: Medio a la derecha (Margen del 5% del ancho del mundo)
        float margenHorizontal = WORLD_WIDTH * 0.05f;
        float mazoX = WORLD_WIDTH - mazoAncho - margenHorizontal;
        float mazoY = (WORLD_HEIGHT - mazoAlto) / 2f;
        animacion.render(batch);
        batch.draw(mazoSprite, mazoX, mazoY, mazoAncho, mazoAlto);


        this.batch.end();

        // 2. DIBUJAR LAS CARTAS (DELEGADO AL MANAGER)
        this.batch.setProjectionMatrix(viewport.getCamera().combined);
        manoManager.render();
    }

    public void update(float delta){
        animacion.update(delta);

        // Desactiva el estado 'inicioRonda' cuando la animación termina
        if (inicioRonda) {
            this.partida.repartirCartas(jugadores.get(0), jugadores.get(1));
            for(int i = 0; i < 3; i++) {
                System.out.println("Partida iniciada. Carta del Jugador: " + this.jugadores.get(0).getMano()[i].getNombre());
            }
            manoManager.inicializarMano();
            animacion.iniciarAnimacionReparto();
            inicioRonda = false;
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
    }
}