package elementos;

public abstract class Carta {

    private final int ID;
    private int valor;
    private Palo palo;
    private int jerarquia;

    Carta(final int ID, int valor, Palo palo, int jerarquia) {
        this.ID = ID;
        this.valor = valor;
        this.jerarquia = jerarquia;
        this.palo = palo;
    }

    public int getJerarquia() {
        return jerarquia;
    }
    public String getNombre() {
        return valor + " de " + palo.getNombre();
    }
}
