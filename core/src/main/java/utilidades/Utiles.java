package utilidades;

import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public abstract class Utiles {
public static Scanner s= new Scanner(System.in);
public static Random r= new Random();
private Utiles() {
	
}
	public static int ingresarEntero(final int MIN,final int MAX) {
		int num=0;
		boolean error=false;
		do {
			error=false;
			try {
				num=s.nextInt();
				if(num<MIN||num>MAX) {
					System.out.println("numero fuera del rango "+MIN+"-"+MAX);
					error=true;
				}
			}catch(InputMismatchException e) {
				System.out.println("tipo de dato mal ingresado");
				error=true;

			}catch(Exception e) {
				System.out.println("error no contemplado");
			}
			
		}while(error);
		return num;
	}

}
