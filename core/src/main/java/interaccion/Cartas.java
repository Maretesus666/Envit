package interaccion;

public class Cartas {

	private String[][] maso ={
		    {"1", "Espada", "1"},
		    {"1", "Basto", "2"},
		    {"7", "Espada", "3"},
		    {"7", "Oro", "4"},

		    {"3", "Espada", "5"},
		    {"3", "Basto", "5"},
		    {"3", "Oro", "5"},
		    {"3", "Copa", "5"},

		    {"2", "Espada", "6"},
		    {"2", "Basto", "6"},
		    {"2", "Oro", "6"},
		    {"2", "Copa", "6"},

		    {"1", "Oro", "7"},
		    {"1", "Copa", "7"},

		    {"12", "Espada", "8"},
		    {"12", "Basto", "8"},
		    {"12", "Oro", "8"},
		    {"12", "Copa", "8"},

		    {"11", "Espada", "9"},
		    {"11", "Basto", "9"},
		    {"11", "Oro", "9"},
		    {"11", "Copa", "9"},

		    {"10", "Espada", "10"},
		    {"10", "Basto", "10"},
		    {"10", "Oro", "10"},
		    {"10", "Copa", "10"},

		    {"7", "Basto", "11"},
		    {"7", "Copa", "11"},

		    {"6", "Espada", "12"},
		    {"6", "Basto", "12"},
		    {"6", "Oro", "12"},
		    {"6", "Copa", "12"},

		    {"5", "Espada", "13"},
		    {"5", "Basto", "13"},
		    {"5", "Oro", "13"},
		    {"5", "Copa", "13"},

		    {"4", "Espada", "14"},
		    {"4", "Basto", "14"},
		    {"4", "Oro", "14"},
		    {"4", "Copa", "14"}
		};

	int[] cartasRep;
	private int cartasTotalesN=3;
	private int cartasTotalesE=3;
	private String[] cartasNos= new String[cartasTotalesN];
	private String[] cartasEl= new String[cartasTotalesE];

	Cartas(int[] cartasRep){
		this.cartasRep=cartasRep;
	}
	
	


	public void mostrarCartas() {
	System.out.println("las cartas repartidas son: ");
		for (int i = 0; i < cartasRep.length; i++) {
		System.out.println(maso[cartasRep[i]][0]+" de "+maso[cartasRep[i]][1]);
	}
	}




	public void jugarCartas() {
		System.out.println("jugador 1 elija sus cartas:");
		for (int i = 0; i < cartasRep.length; i+=2) {
			System.out.println("carta "+(i+1)+": "+maso[cartasRep[i]][0]+" de "+maso[cartasRep[i]][1]);
		}
		
		System.out.println("jugador 2 elija sus cartas:");
		for (int i = 1; i < cartasRep.length; i+=2) {
			System.out.println("carta "+(i+1)+": "+maso[cartasRep[i]][0]+" de "+maso[cartasRep[i]][1]);
		}
	}
}


 
 