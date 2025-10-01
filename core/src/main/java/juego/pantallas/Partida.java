package juego.pantallas;
import juego.elementos.*;
import juego.personajes.Jugador;
import juego.utilidades.Aleatorio;

import java.util.ArrayList;

public class Partida {

    private ArrayList<Carta> cartasRepartidas = new ArrayList<>();
    private Mazo mazo = new Mazo();

    public void jugar(Jugador jugador1, Jugador jugador2) {
        repartirCartas(jugador1, jugador2);
        finalizarRonda(jugador1, jugador2);
    }

    public void finalizarRonda(Jugador jugador1, Jugador jugador2) {
        cartasRepartidas.clear();
        jugador1.limpiarMazo();
        jugador2.limpiarMazo();
    }


    public void repartirCartas(Jugador jugador1, Jugador jugador2) {
        for (int i = 0; i < 3; i++) {
            repartirUnaCartaA(jugador1);
            repartirUnaCartaA(jugador2);
        }
    }


    private void repartirUnaCartaA(Jugador jugador) {
        Carta cartaElegida;
        boolean existe;

        do {
            cartaElegida = sacarCarta();
            existe = verificarExistenciaCarta(cartaElegida);
        } while (existe);

        jugador.agregarCarta(cartaElegida);
        cartasRepartidas.add(cartaElegida);
    }

    public boolean verificarExistenciaCarta(Carta carta) {
        return cartasRepartidas.contains(carta);
    }

    public Carta sacarCarta() {

        int num = Aleatorio.generarEntero(this.mazo.getCantCartas());
        return this.mazo.getCarta(num);

    }

}


