package src.interaccion;

import personajes.Jugador1;
import personajes.Jugador2; 
import utilidades.Utiles;

public class Partida { 
	private int puntosNos=0;
	private int puntosEl=0;
	private int cartasTotales=6;
	private int[] totalCartRep= new int[cartasTotales];
	
	private String[] mano;
	
	public void empezarPartida(Jugador1 jugador1,Jugador2 jugador2) {
		System.out.println("11");
		System.out.println("bienvenidos "+jugador1.getNombre()+" y "+ jugador2.getNombre());
		System.out.println("Que empieze la partida");
		 
		totalCartRep= repartirCartas();
		Cartas cartas= new Cartas(totalCartRep);
		cartas.mostrarCartas();
		cartas.jugarCartas();
		
	}
	

		
		 

	private int[] repartirCartas() {
	 int cartaDada=0;
	 int cantCartD=0; 
	int[] cartasRepartidas= new int[cartasTotales];
for (int i = 0; i < cartasRepartidas.length; i++) {
	cartasRepartidas[i]=-1;
}
	boolean repetido=false;
	
		while(cantCartD<cartasTotales) {
			int i=0;	
			cartaDada= Utiles.r.nextInt(40);
			repetido=false;
			while(i<cartasTotales) {
				if(cartasRepartidas[i]==cartaDada) {
					repetido= true;
					break;
				}
				i++;
		 	
		};
		if(!repetido) {
			cartasRepartidas[cantCartD]=cartaDada;
			cantCartD++;
		};
		
		
		
	}






 
		return cartasRepartidas;
	}
	
	
}
