package elementos;

public abstract class Carta {

    private int valor;
    private Palo palo;
    private int jerarquia;

    Carta(int valor, Palo palo, int jerarquia) {
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
