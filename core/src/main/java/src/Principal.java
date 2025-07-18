package src;

import interaccion.Partida;
import personajes.Jugador1;
import personajes.Jugador2;
import utilidades.Utiles;

public class Principal {

	public static void main(String[] args) { 
		
		Jugador1 jugador1;
		Jugador2 jugador2;
		String nomJugador1;
		String nomJugador2;
		Partida partida=new Partida();
		int opc;
		boolean salir=false;
		System.out.println("v11");
		System.out.println("+----Envit----+");
		System.out.println("introdusca nombre de jugador 1");
		nomJugador1=Utiles.s.nextLine();
		System.out.println("introdusca nombre de jugador 2");
		nomJugador2=Utiles.s.nextLine();
		jugador1= new Jugador1(nomJugador1);
		jugador2= new Jugador2(nomJugador2);
		
		
		do {
			System.out.println("ingrese que desea hacer");
			System.out.println("1.partida");
			System.out.println("2.salir");
			opc=Utiles.ingresarEntero(1, 2);
			switch(opc){
				case 1:
					partida.empezarPartida(jugador1,jugador2);break;
				case 2:
					salir=false; break;
				default: System.out.println("error"); break;
			}
		}while(salir);
		Utiles.s.close();
	}

}
