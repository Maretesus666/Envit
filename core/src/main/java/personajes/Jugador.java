package personajes;

import elementos.Carta;

 public class Jugador {
	private String nombre;
	private int puntos;
	private Carta mano[] = new Carta[3];
	private int cantCartas = 0;

	public String getNombre() {
		return nombre;
	}

	public Carta getMano() {
		return mano;
	}

	public int getPuntos() {
		return puntos;
	}

	public void agregarCarta(Carta c){
		this.mano[cantCartas] = c;
		cantCartas++;
	}

	public void limpiarMazo(){
		cantCartas = 0;
	}
}