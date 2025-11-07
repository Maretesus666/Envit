package juego.elementos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CartaRenderer {

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final Viewport viewport;
    private final Color backgroundColor = new Color(0.95f, 0.95f, 0.95f, 1);

    public CartaRenderer(SpriteBatch batch, ShapeRenderer shapeRenderer, Viewport viewport) {
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
        this.viewport = viewport;
    }

    public void render(Carta carta, float x, float y, float width, float height) {

        carta.updateLimites(x, y, width, height);
        Rectangle limites = carta.getLimites();

        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(backgroundColor);

        float lx = limites.x;
        float ly = limites.y;
        float lw = limites.width;
        float lh = limites.height;

        // corner en píxeles (radio)
        int corner = Math.max(2, Math.min(16, (int) Math.round(Math.min(lw, lh) * 0.12f)));
        float r = corner; // usar float radius = corner para arc

        // Centro (rectángulo central que deja espacio para esquinas)
        shapeRenderer.rect(lx + r, ly + r, lw - 2f * r, lh - 2f * r);

        // Bordes (rectángulos que no tocan las esquinas)
        shapeRenderer.rect(lx + r, ly, lw - 2f * r, r); // abajo
        shapeRenderer.rect(lx + r, ly + lh - r, lw - 2f * r, r); // arriba
        shapeRenderer.rect(lx, ly + r, r, lh - 2f * r); // izquierda
        shapeRenderer.rect(lx + lw - r, ly + r, r, lh - 2f * r); // derecha

        // Esquinas: dibujamos cuatro arcs (cuartos de círculo) rellenados.
        // En libGDX: arc(x,y, radius, startDeg, sweepDeg) donde 0° = +X, aumenta CCW.
        // bottom-left (desde 180° a 270°)
        shapeRenderer.arc(lx + r, ly + r, r, 180f, 90f);

        // bottom-right (desde 270° a 360°)
        shapeRenderer.arc(lx + lw - r, ly + r, r, 270f, 90f);

        // top-left (desde 90° a 180°)
        shapeRenderer.arc(lx + r, ly + lh - r, r, 90f, 90f);

        // top-right (desde 0° a 90°)
        shapeRenderer.arc(lx + lw - r, ly + lh - r, r, 0f, 90f);

        shapeRenderer.end();

        batch.begin();
        carta.draw(batch, x, y, width, height);
        batch.end();
    }
}
