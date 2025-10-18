package juego.elementos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import juego.personajes.Jugador;

/**
 * HUD (Heads-Up Display) que muestra información de la partida:
 * - Puntos del jugador
 * - Puntos del rival
 * - Mano actual (1/3, 2/3, 3/3)
 */
public class Hud {

    private BitmapFont font;
    private Jugador jugador;
    private Jugador rival;

    private float worldWidth;
    private float worldHeight;

    // Colores
    private Color colorJugador = new Color(0.2f, 0.8f, 0.2f, 1f); // Verde
    private Color colorRival = new Color(0.9f, 0.3f, 0.3f, 1f);   // Rojo
    private Color colorNeutral = new Color(0.9f, 0.9f, 0.7f, 1f); // Amarillo claro

    // Posiciones
    private float margen = 20f;

    public Hud(BitmapFont font, Jugador jugador, Jugador rival, float worldWidth, float worldHeight) {
        this.font = font;
        this.jugador = jugador;
        this.rival = rival;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    /**
     * Dibuja el HUD completo
     */
    public void render(SpriteBatch batch, int manoActual, boolean esTurnoJugador) {
        batch.begin();

        // Panel de puntos del jugador (abajo izquierda)
        dibujarPuntosJugador(batch);

        // Panel de puntos del rival (arriba izquierda)
        dibujarPuntosRival(batch);

        // Información de mano actual (arriba centro)
        dibujarInfoMano(batch, manoActual);

        // Indicador de turno (centro derecha)
        dibujarIndicadorTurno(batch, esTurnoJugador);

        batch.end();
    }

    /**
     * Dibuja los puntos del jugador
     */
    private void dibujarPuntosJugador(SpriteBatch batch) {
        font.setColor(colorJugador);
        font.getData().setScale(1.5f);

        String textoJugador = "TU: " + jugador.getPuntos() + " pts";

        // Abajo izquierda
        float x = margen;
        float y = margen + 30;

        font.draw(batch, textoJugador, x, y);
    }

    /**
     * Dibuja los puntos del rival
     */
    private void dibujarPuntosRival(SpriteBatch batch) {
        font.setColor(colorRival);
        font.getData().setScale(1.5f);

        String textoRival = "RIVAL: " + rival.getPuntos() + " pts";

        // Arriba izquierda
        float x = margen;
        float y = worldHeight - margen;

        font.draw(batch, textoRival, x, y);
    }

    /**
     * Dibuja la información de la mano actual
     */
    private void dibujarInfoMano(SpriteBatch batch, int manoActual) {
        if (manoActual < 0 || manoActual > 2) {
            return; // No mostrar si no hay mano en curso
        }

        font.setColor(colorNeutral);
        font.getData().setScale(1.2f);

        String textoMano = "MANO " + (manoActual +1) + "/3";

        // Arriba centro
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout =
                new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, textoMano);

        float x = (worldWidth - layout.width) / 2f;
        float y = worldHeight - margen;

        font.draw(batch, textoMano, x, y);
    }

    /**
     * Dibuja un indicador visual de quién tiene el turno
     */
    private void dibujarIndicadorTurno(SpriteBatch batch, boolean esTurnoJugador) {
        font.getData().setScale(1.0f);

        String texto;
        Color color;

        if (esTurnoJugador) {
            texto = "TU TURNO";
            color = colorJugador;
        } else {
            texto = "RIVAL JUGANDO...";
            color = colorRival;
        }

        font.setColor(color);

        // Derecha centro
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout =
                new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, texto);

        float x = worldWidth - layout.width - margen;
        float y = worldHeight / 2f;

        font.draw(batch, texto, x, y);
    }

    /**
     * Dibuja un mensaje temporal grande en el centro (para anuncios)
     */
    public void dibujarMensajeCentral(SpriteBatch batch, String mensaje, Color color) {
        batch.begin();

        font.setColor(color);
        font.getData().setScale(2.5f);

        com.badlogic.gdx.graphics.g2d.GlyphLayout layout =
                new com.badlogic.gdx.graphics.g2d.GlyphLayout(font, mensaje);

        float x = (worldWidth - layout.width) / 2f;
        float y = (worldHeight + layout.height) / 2f;

        font.draw(batch, mensaje, x, y);

        batch.end();
    }



}