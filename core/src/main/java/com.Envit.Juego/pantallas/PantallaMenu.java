package com.Envit.Juego.pantallas;

import com.Envit.Juego.Principal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class PantallaMenu implements Screen {

    private final Principal game;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    private Texture backgroundTexture;
    private Texture optionsBackgroundTexture;

    private boolean inOptionsMode = false;

    // Efectos CRT
    private boolean crtEnabled = true;
    private boolean flickerEnabled = true;
    private boolean shakeEnabled = true;

    private float scanlineOffset = 0f;
    private float crtFlicker = 0f;

    private Random random = new Random();

    // UI
    // Eliminados: TextButton, CheckBox, Label, Skin
    // En su lugar, usaremos tus propios assets y lógica manual

    // Ejemplo de posiciones para tus botones
    private Rectangle btnPlayRect = new Rectangle();
    private Rectangle btnOptionsRect = new Rectangle();
    private Rectangle btnExitRect = new Rectangle();
    private Rectangle btnCloseOptionsRect = new Rectangle();
    private Rectangle chkCRTBox = new Rectangle();
    private Rectangle chkFlickerBox = new Rectangle();
    private Rectangle chkShakeBox = new Rectangle();

    // Texturas para tus botones (deberás poner los nombres correctos de tus archivos)
    private Texture btnPlayTexture, btnOptionsTexture, btnExitTexture, btnCloseOptionsTexture;
    private Texture chkCheckedTexture, chkUncheckedTexture;
    private Texture titleTexture;

    public PantallaMenu(final Principal game) {
        this.game = game;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        // Stage eliminado, ya no se usa Scene2D
        // Cargar tus texturas personalizadas
        loadFont();
        loadBackgrounds();
        loadButtonTextures();
        setButtonRects();
    }

    private void loadFont() {
        try {
            // Quitar "assets/" de la ruta, solo poner la ruta relativa desde assets
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fuentes/medieval.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.size = 36;
            font = generator.generateFont(parameter);
            generator.dispose();
        } catch (Exception e) {
            font = new BitmapFont(); // fallback default
            Gdx.app.log("PantallaMenu", "No se pudo cargar la fuente medieval.ttf, usando fuente por defecto");
        }
    }

    private void loadBackgrounds() {
        // Quitar "assets/" de la ruta, solo poner la ruta relativa desde assets
        if (Gdx.files.internal("fondos/fondo.png").exists()) {
            backgroundTexture = new Texture(Gdx.files.internal("fondos/fondo.png"));
            Gdx.app.log("PantallaMenu", "Fondo principal cargado correctamente");
        } else {
            backgroundTexture = null;
            Gdx.app.error("PantallaMenu", "No se encontró fondos/fondo.png, usando fondo procedural");
        }
        if (Gdx.files.internal("fondos/fondoOpciones.png").exists()) {
            optionsBackgroundTexture = new Texture(Gdx.files.internal("fondos/fondoOpciones.png"));
            Gdx.app.log("PantallaMenu", "Fondo de opciones cargado correctamente");
        } else {
            optionsBackgroundTexture = null;
            Gdx.app.error("PantallaMenu", "No se encontró fondos/fondoOpciones.png, usando fondo procedural para opciones");
        }
    }

    private void loadButtonTextures() {
     /*   // Cambia los nombres por los de tus archivos reales
        btnPlayTexture = tryLoadTexture("sprites/btn_jugar.png");
        btnOptionsTexture = tryLoadTexture("sprites/btn_jugar.png");
        btnExitTexture = tryLoadTexture("sprites/btn_jugar.png");
        btnCloseOptionsTexture = tryLoadTexture("sprites/btn_jugar.png");
        chkCheckedTexture = tryLoadTexture("sprites/btn_jugar.png");
        chkUncheckedTexture = tryLoadTexture("sprites/pepi.png");
        titleTexture = tryLoadTexture("sprites/btn_jugar.png");
    */}

    private Texture tryLoadTexture(String path) {
        if (Gdx.files.internal(path).exists()) {
            return new Texture(Gdx.files.internal(path));
        } else {
            Gdx.app.error("PantallaMenu", "No se encontró: " + path);
            return null;
        }
    }

    private void setButtonRects() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        float btnW = 200, btnH = 50;
        float centerX = w / 2f - btnW / 2f;
        btnPlayRect.set(centerX, 400, btnW, btnH);
        btnOptionsRect.set(centerX, 320, btnW, btnH);
        btnExitRect.set(centerX, 240, btnW, btnH);
        btnCloseOptionsRect.set(centerX, 180, btnW, btnH);
        // Checkboxes
        float chkW = 32, chkH = 32;
        float chkX = w / 2f - 100;
        chkCRTBox.set(chkX, 320, chkW, chkH);
        chkFlickerBox.set(chkX, 280, chkW, chkH);
        chkShakeBox.set(chkX, 240, chkW, chkH);
    }

    @Override
    public void resize(int width, int height) {
        setButtonRects();
    }

    @Override
    public void render(float delta) {
        // Actualizar lógica efectos CRT
        if (crtEnabled) {
            scanlineOffset += 60 * delta * 0.5f;
            if (scanlineOffset > 4) scanlineOffset = 0;
        }

        if (flickerEnabled) {
            crtFlicker += 60 * delta * 0.1f;
            if (crtFlicker > Math.PI * 2) crtFlicker = 0;
        }

        // Limpiar pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // Dibujar fondo (según modo)
        if (!inOptionsMode) {
            if (backgroundTexture != null) {
                batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            } else {
                drawProceduralBackground(batch);
            }
        } else {
            if (optionsBackgroundTexture != null) {
                batch.draw(optionsBackgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            } else {
                drawProceduralOptionsBackground(batch);
            }
        }

        // Dibuja el título
        if (titleTexture != null) {
            batch.draw(titleTexture, Gdx.graphics.getWidth()/2f - titleTexture.getWidth()/2f, Gdx.graphics.getHeight() - 120);
        } else if (font != null) {
            font.setColor(Color.CYAN);
            font.getData().setScale(2f);
            font.draw(batch, "Envit", Gdx.graphics.getWidth()/2f - 80, Gdx.graphics.getHeight() - 80);
        }

        // Dibuja los botones principales
        if (!inOptionsMode) {
            drawButton(batch, btnPlayTexture, btnPlayRect, "JUGAR");
            drawButton(batch, btnOptionsTexture, btnOptionsRect, "OPCIONES");
            drawButton(batch, btnExitTexture, btnExitRect, "SALIR");
        } else {
            drawButton(batch, btnCloseOptionsTexture, btnCloseOptionsRect, "CERRAR");
            // Checkboxes
            drawCheckbox(batch, chkCRTBox, crtEnabled, "Efectos CRT");
            drawCheckbox(batch, chkFlickerBox, flickerEnabled, "Destellos");
            drawCheckbox(batch, chkShakeBox, shakeEnabled, "Temblor");
        }
        batch.end();

        // Dibujar efecto CRT
        if (crtEnabled) {
            drawCRTEffect();
        }

        // Manejo de input manual para botones y checkboxes
        handleInput();
    }

    private void drawButton(SpriteBatch batch, Texture texture, Rectangle rect, String texto) {
        boolean hovered = rect.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        if (texture != null) {
            batch.setColor(hovered ? Color.CYAN : Color.WHITE);
            batch.draw(texture, rect.x, rect.y, rect.width, rect.height);
            batch.setColor(Color.WHITE);
        } else if (font != null) {
            // Fondo simple para el botón
            batch.setColor(hovered ? new Color(0,1,1,0.5f) : new Color(0,0,0,0.5f));
            batch.draw(getWhitePixel(), rect.x, rect.y, rect.width, rect.height);
            batch.setColor(Color.WHITE);
            font.setColor(hovered ? Color.CYAN : Color.WHITE);
            font.getData().setScale(1.2f);
            font.draw(batch, texto, rect.x + rect.width/2 - 50, rect.y + rect.height/2 + 15);
        }
    }

    private void drawCheckbox(SpriteBatch batch, Rectangle rect, boolean checked, String label) {
        boolean hovered = rect.contains(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY());
        Texture tex = checked ? chkCheckedTexture : chkUncheckedTexture;
        if (tex != null) {
            batch.setColor(hovered ? Color.CYAN : Color.WHITE);
            batch.draw(tex, rect.x, rect.y, rect.width, rect.height);
            batch.setColor(Color.WHITE);
        } else {
            batch.setColor(hovered ? new Color(0,1,1,0.5f) : new Color(0,0,0,0.5f));
            batch.draw(getWhitePixel(), rect.x, rect.y, rect.width, rect.height);
            batch.setColor(Color.WHITE);
        }
        if (font != null) {
            font.setColor(hovered ? Color.CYAN : Color.WHITE);
            font.getData().setScale(1f);
            font.draw(batch, label, rect.x + rect.width + 8, rect.y + rect.height - 8);
        }
    }

    private void handleInput() {
        if (Gdx.input.justTouched()) {
            float x = Gdx.input.getX();
            float y = Gdx.graphics.getHeight() - Gdx.input.getY();
            if (!inOptionsMode) {
                if (btnPlayRect.contains(x, y)) startGame();
                else if (btnOptionsRect.contains(x, y)) toggleOptions();
                else if (btnExitRect.contains(x, y)) Gdx.app.exit();
            } else {
                if (btnCloseOptionsRect.contains(x, y)) toggleOptions();
                else if (chkCRTBox.contains(x, y)) crtEnabled = !crtEnabled;
                else if (chkFlickerBox.contains(x, y)) flickerEnabled = !flickerEnabled;
                else if (chkShakeBox.contains(x, y)) shakeEnabled = !shakeEnabled;
            }
        }
    }

    private void toggleOptions() {
        inOptionsMode = !inOptionsMode;
    }

    private void startGame() {
        // Aquí llamás para cambiar de pantalla a la partida (ejemplo)
        // game.setScreen(new PantallaPartida(game));

        // Por ahora mostramos mensaje y volvemos al menú
        Gdx.app.log("PantallaMenu", "Iniciar partida - funcionalidad no implementada");
    }

    private void drawProceduralBackground(SpriteBatch batch) {
        // Para algo simple, dibujamos un fondo con gradient manual (puede ser con ShapeRenderer si querés)
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        // Gradient vertical manual simulando tu fondo original
        shapeRenderer.rect(0, 0, w, h, new Color(0.04f, 0.04f, 0.12f,1), new Color(0.16f, 0.04f, 0.24f,1), new Color(0.24f, 0.08f, 0.39f,1), new Color(0.04f, 0.16f, 0.39f,1));

        // Elementos gráficos retro simples
        for (int i = 0; i < 15; i++) {
            float x = random.nextInt(w);
            float y = random.nextInt(h);
            float size = random.nextInt(150) + 50;
            shapeRenderer.setColor(0, 1, 1, 0.12f);
            shapeRenderer.circle(x, y, size);
        }

        // Líneas de circuito
        shapeRenderer.setColor(1, 0, 1, 0.16f);
        for (int i = 0; i < 20; i++) {
            float x1 = random.nextInt(w);
            float y1 = random.nextInt(h);
            float x2 = x1 + random.nextInt(200) - 100;
            float y2 = y1 + random.nextInt(200) - 100;
            shapeRenderer.rectLine(x1, y1, x2, y2, 2);
        }

        shapeRenderer.end();

        batch.begin();
    }

    private void drawProceduralOptionsBackground(SpriteBatch batch) {
        batch.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        // Gradientes para opciones (como en tu código)
        // Parte 1
        shapeRenderer.rect(0, 0, w / 2, h / 2, new Color(0.04f, 0.04f, 0.12f,1), new Color(0.16f, 0.04f, 0.24f,1), new Color(0.24f, 0.08f, 0.39f,1), new Color(0.04f, 0.16f, 0.39f,1));
        // Parte 2
        shapeRenderer.rect(w / 2, 0, w / 2, h, new Color(0.24f, 0.08f, 0.39f, 0.39f), new Color(0.08f, 0.16f, 0.39f, 0.39f), new Color(0.08f, 0.16f, 0.39f, 0.39f), new Color(0.24f, 0.08f, 0.39f, 0.39f));

        shapeRenderer.end();

        batch.begin();
    }

    private void drawCRTEffect() {
        // batch.end(); // QUITADO: ya está terminado antes de llamar a este método

        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();

        // Líneas de escaneo horizontales
        shapeRenderer.setColor(0, 0, 0, 0.15f);
        for (int y = (int) scanlineOffset; y < h; y += 3) {
            shapeRenderer.rect(0, y, w, 1);
        }

        // Líneas verticales ocasionales
        if (random.nextInt(50) < 2) {
            shapeRenderer.setColor(0, 0, 0, 0.08f);
            for (int x = 0; x < w; x += 2) {
                shapeRenderer.rect(x, 0, 1, h);
            }
        }

        shapeRenderer.end();

        // batch.begin(); // QUITADO: el control de batch está en render()

        // Flicker
        if (flickerEnabled && random.nextInt(100) < 8) {
            batch.begin();
            Color flickerColor = new Color(1,1,1, 0.06f + 0.1f * (float)Math.sin(crtFlicker));
            batch.setColor(flickerColor);
            batch.draw(getWhitePixel(), 0, 0, w, h);
            batch.setColor(Color.WHITE);
            batch.end();
        }

        // Ruido estático
        if (random.nextInt(150) < 3) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            for (int i = 0; i < 80; i++) {
                float x = random.nextInt(w);
                float y = random.nextInt(h);
                float alpha = random.nextFloat() * 0.3f;
                shapeRenderer.setColor(1, 1, 1, alpha);
                shapeRenderer.rect(x, y, 2, 2);
            }
            shapeRenderer.end();
        }

        // Distorsión horizontal ocasional (temblor)
        if (shakeEnabled && random.nextInt(300) < 2) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            int distortY = random.nextInt(h);
            shapeRenderer.setColor(1, 1, 1, 0.12f);
            shapeRenderer.rect(0, distortY, w, 3);
            shapeRenderer.end();
        }
    }

    // Textura 1x1 blanca para dibujar rectángulos simples
    private Texture whitePixel;

    private Texture getWhitePixel() {
        if (whitePixel == null) {
            Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
            pixmap.setColor(Color.WHITE);
            pixmap.fill();
            whitePixel = new Texture(pixmap);
            pixmap.dispose();
        }
        return whitePixel;
    }

    @Override
    public void pause() {
        // no necesario
    }

    @Override
    public void resume() {
        // no necesario
    }

    @Override
    public void hide() {
        // no necesario
    }

    @Override
    public void show() {
        // No es necesario implementar nada específico aquí para este menú
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        if (backgroundTexture != null) backgroundTexture.dispose();
        if (optionsBackgroundTexture != null) optionsBackgroundTexture.dispose();
        if (whitePixel != null) whitePixel.dispose();
        if (font != null) font.dispose();
        if (btnPlayTexture != null) btnPlayTexture.dispose();
        if (btnOptionsTexture != null) btnOptionsTexture.dispose();
        if (btnExitTexture != null) btnExitTexture.dispose();
        if (btnCloseOptionsTexture != null) btnCloseOptionsTexture.dispose();
        if (chkCheckedTexture != null) chkCheckedTexture.dispose();
        if (chkUncheckedTexture != null) chkUncheckedTexture.dispose();
        if (titleTexture != null) titleTexture.dispose();
    }
}
