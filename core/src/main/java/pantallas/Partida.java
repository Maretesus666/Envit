package pantallas;

import elementos.*;
import personajes.Jugador;
import utilidades.Aleatorio;

public class Partida {
    private Jugador jugador1;
    private Jugador jugador2;


    public void repartirCartas() {
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
    }

    public boolean verificarExistenciaCarta(Carta carta){
        int i = 0;
        boolean encontrado = false;
        while(!encontrado){
            if(jugador1.getMano()[i] == carta){
                encontrado = true;
            } else if (jugador2.getMano()[i] == carta) {
                encontrado = true;
            }
            i++;
        }
        return encontrado;
    }

    public Carta sacarCarta(){
        Carta carta;
        int num = Aleatorio.generarEntero(40);

        if (num == 1) carta = new CuatroCopa();
        else if (num == 2) carta = new CuatroOro();
        else if (num == 3) carta = new CuatroEspada();
        else if (num == 4) carta = new CuatroBasto();

        else if (num == 5) carta = new CincoCopa();
        else if (num == 6) carta = new CincoOro();
        else if (num == 7) carta = new CincoEspada();
        else if (num == 8) carta = new CincoBasto();

        else if (num == 9) carta = new SeisCopa();
        else if (num == 10) carta = new SeisOro();
        else if (num == 11) carta = new SeisEspada();
        else if (num == 12) carta = new SeisBasto();

        else if (num == 13) carta = new SieteCopa();
        else if (num == 14) carta = new SieteOro();
        else if (num == 15) carta = new SieteEspada();
        else if (num == 16) carta = new SieteBasto();

        else if (num == 17) carta = new DiezCopa();
        else if (num == 18) carta = new DiezOro();
        else if (num == 19) carta = new DiezEspada();
        else if (num == 20) carta = new DiezBasto();

        else if (num == 21) carta = new OnceCopa();
        else if (num == 22) carta = new OnceOro();
        else if (num == 23) carta = new OnceEspada();
        else if (num == 24) carta = new OnceBasto();

        else if (num == 25) carta = new DoceCopa();
        else if (num == 26) carta = new DoceOro();
        else if (num == 27) carta = new DoceEspada();
        else if (num == 28) carta = new DoceBasto();

        else if (num == 29) carta = new UnoCopa();
        else if (num == 30) carta = new UnoOro();
        else if (num == 31) carta = new UnoEspada();
        else if (num == 32) carta = new UnoBasto();

        else if (num == 33) carta = new DosCopa();
        else if (num == 34) carta = new DosOro();
        else if (num == 35) carta = new DosEspada();
        else if (num == 36) carta = new DosBasto();

        else if (num == 37) carta = new TresCopa();
        else if (num == 38) carta = new TresOro();
        else if (num == 39) carta = new TresEspada();
        else if (num == 40) carta = new TresBasto();

        return carta;
    }


}
