package juego.elementos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
public class Carta {

    private static final Texture barajaTexture = new Texture(Gdx.files.internal("sprites/baraja2.png"));;
    private int valor;
    private Palo palo;
    private int jerarquia;
    private TextureRegion region;
    private Rectangle limites;

    public Carta(int valor, Palo palo) {
        this.valor = valor;
        this.palo = palo;
        this.asignarPeso(valor,palo);

        int cartaW = barajaTexture.getWidth() / 10;
        int cartaH = barajaTexture.getHeight() / 4;
        int colIndex;
        if (valor == 1) {
            colIndex = 9;
        } else if (valor >= 2 && valor <= 7) {
            colIndex = valor - 2;
        } else if (valor >= 10 && valor <= 12) {
            colIndex = valor - 4;
        } else {
            colIndex = 0;
        }


        int rowIndex;
        if (palo == Palo.COPAS) {
            rowIndex = 0; // Primera fila
        } else if (palo == Palo.BASTO) {
            rowIndex = 1; // Segunda fila
        } else if (palo == Palo.ORO) {
            rowIndex = 2; // Tercera fila
        } else if (palo == Palo.ESPADAS) {
            rowIndex = 3; // Cuarta fila
        } else {
            rowIndex = 0; // Valor por defecto, aunque no debería usarse
        }

        // 3. CALCULAR COORDENADAS DE PIXEL
        int startX = colIndex * cartaW;
        int startY = rowIndex * cartaH;

        // 4. CREAR LA TEXTURE REGION
        this.region = new TextureRegion(
                barajaTexture,
                startX,
                startY,
                cartaW,
                cartaH
        );

        this.limites = new Rectangle(0, 0, cartaW, cartaH);
    }

    Carta( int valor, Palo palo, int jerarquia) {
        this.valor = valor;
        this.palo = palo;
        this.jerarquia = jerarquia;
    }

    public void draw(SpriteBatch batch, float x, float y, float width, float height) {
        batch.draw(this.region, x, y, width, height);
    }

    private int asignarPeso(int valor, Palo palo) {
        // La jerarquía se define de mayor a menor peso (1 es el más alto)

        // 1. ANCHOS Y SIETES ESPECIALES
        if (valor == 1 && palo == Palo.ESPADAS) return 1;  // Ancho de Espadas: 1
        if (valor == 1 && palo == Palo.BASTO) return 2;   // Ancho de Basto: 2
        if (valor == 7 && palo == Palo.ESPADAS) return 3;  // Siete de Espadas: 3
        if (valor == 7 && palo == Palo.ORO) return 4;     // Siete de Oro: 4

        // 2. TRES Y DOS
        if (valor == 3) return 5;                         // 3: 5
        if (valor == 2) return 6;                         // 2: 6

        // 3. ANCHOS COMUNES
        if (valor == 1) return 7;                         // Ancho de Copa/Oro: 7

        // 4. CARTAS NEGRAS (10, 11, 12, que asumo son Sota, Caballo, Rey)
        // Nota: Asumo que 10=Sota, 11=Caballo, 12=Rey, y que tu loop en Mazo
        // genera valores de 1 a 12 (excluyendo 8 y 9). Ajusta los valores
        // a 12/11/10 si usas Sota/Caballo/Rey como 10/11/12.

        // Si valor va de 1 a 12, y 10, 11, 12 son las "figuras":
        if (valor == 12) return 8; // Rey: 8
        if (valor == 11) return 9; // Caballo: 9
        if (valor == 10) return 10; // Sota (o 10): 10

        // 5. SIETES COMUNES
        if (valor == 7) return 11; // 7 de Basto/Copa: 11

        // 6. CARTAS MEDIAS (6, 5, 4)
        if (valor == 6) return 12; // 6: 12
        if (valor == 5) return 13; // 5: 13
        if (valor == 4) return 14; // 4: 14 (El de menor peso)

        // Si por alguna razón llega aquí (no debería con tu Mazo), devolvemos -1 o lanzamos error
        return -1;
    }

    public void updateLimites(float x, float y, float width, float height) {
        this.limites.set(x, y, width, height);
    }

    public int getJerarquia() {
        return jerarquia;
    }
    public String getNombre() {
        return valor + " de " + palo;
    }

    public Rectangle getLimites() {
        return limites;
    }
}
