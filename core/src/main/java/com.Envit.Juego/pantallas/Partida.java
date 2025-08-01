package com.Envit.Juego.pantallas;
import com.Envit.Juego.elementos.*;
import com.Envit.Juego.personajes.Jugador;
import com.Envit.Juego.utilidades.Aleatorio;

import java.util.ArrayList;

public class Partida {
    private ArrayList<Carta> cartasRepartidas = new ArrayList<>();


    public void jugarRonda(Jugador jugador1, Jugador jugador2){
     repartirCartas(jugador1,jugador2);
     finalizarRonda(jugador1,jugador2);
    }

    public void finalizarRonda(Jugador jugador1, Jugador jugador2){
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

    public boolean verificarExistenciaCarta(Carta carta){
        return cartasRepartidas.contains(carta);
    }

    public Carta sacarCarta(){

            int num = Aleatorio.generarEntero(40);
            switch (num) {
                case 1: return new CuatroCopa();
                case 2: return new CuatroOro();
                case 3: return new CuatroEspada();
                case 4: return new CuatroBasto();

                case 5: return new CincoCopa();
                case 6: return new CincoOro();
                case 7: return new CincoEspada();
                case 8: return new CincoBasto();

                case 9: return new SeisCopa();
                case 10: return new SeisOro();
                case 11: return new SeisEspada();
                case 12: return new SeisBasto();

                case 13: return new SieteCopa();
                case 14: return new SieteOro();
                case 15: return new SieteEspada();
                case 16: return new SieteBasto();

                case 17: return new DiezCopa();
                case 18: return new DiezOro();
                case 19: return new DiezEspada();
                case 20: return new DiezBasto();

                case 21: return new OnceCopa();
                case 22: return new OnceOro();
                case 23: return new OnceEspada();
                case 24: return new OnceBasto();

                case 25: return new DoceCopa();
                case 26: return new DoceOro();
                case 27: return new DoceEspada();
                case 28: return new DoceBasto();

                case 29: return new UnoCopa();
                case 30: return new UnoOro();
                case 31: return new UnoEspada();
                case 32: return new UnoBasto();

                case 33: return new DosCopa();
                case 34: return new DosOro();
                case 35: return new DosEspada();
                case 36: return new DosBasto();

                case 37: return new TresCopa();
                case 38: return new TresOro();
                case 39: return new TresEspada();
                case 40: return new TresBasto();

                default:
                    throw new IllegalStateException("Número de carta inválido: " + num);
            }
        }

    }



