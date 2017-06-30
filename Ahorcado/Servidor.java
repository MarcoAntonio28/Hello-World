import java.net.*;
import java.io.*;
import java.util.*;

public class Servidor
{
	private static ServerSocket servidor = null;
	private static Socket cliente = null;
	private static final int puerto = 1390;
	
	public static void main(String[] args)
	{			// Clase principal
		try
		{
			servidor = new ServerSocket(puerto);
		}
		catch(IOException e)
		{
			System.err.println(e);
		}

		while(true)
		{											// El While siempre sera verdadero para poder dar servicio a varios clientes
			if(servidor != null)
			{
				try
				{
					cliente = servidor.accept();				// El servidor se conecta con un cliente
					System.out.println("Se conecto un cliente");

				}
				catch(IOException e)
				{
					System.err.println(e);
				}
				if(cliente != null)
				{													// Si la variable cliente no es nula se crea un hilo
					Thread hilo = new Thread(new ServidorHilo(cliente));
					hilo.start();										// se inicia el hilo
				}
			}
			
		}
	}
	
}

class ServidorHilo implements Runnable
{				// clase para ejecutar los hilos
	private Socket cliente;
	
	public ServidorHilo(Socket s)
	{
		cliente = s;
	}
	
	public void run()
	{
		Scanner entrada = null;					//variables para comunicarse entre servidor y cliente
		PrintWriter salida = null;
		Ahorcado juego = new Ahorcado();      // se declara un objeto de la clase ahorcado
		
		try
		{
			entrada = new Scanner(new BufferedInputStream(cliente.getInputStream())); // Se inicializa las variavles entrada y salida para comunicarse en tre servidor cliente
			salida = new PrintWriter(cliente.getOutputStream());
		}
		catch(IOException ex)
		{
			System.err.println(ex);
		}
		
		if(entrada != null && salida != null)
		{  															// si la variables entrada y salida contienen algo se inicializa el juego
			juego.jugar(entrada, salida);
		}
	}
}

class Ahorcado
{
	private ArrayList<String> lst = new ArrayList<String>();   //se declara una lista para las palabras que se obtendran de un archivo.
	private String palabra;
	char [] oculta;
	
	private void cargarPalabras()
	{					//esta funcion se encarga de cargar las palabras de un archivo a una lista de palabras declarada anteriormente.
		String linea;
		try
		{
			BufferedReader archivo = new BufferedReader( new FileReader("//home//m-kenshin//Escritorio//N5//archivo.txt")); //se lee un archivo 

			while((linea = archivo.readLine()) != null)
			{  		//mientras el buffer contenga palabras estos se guardaran en la lista.
				lst.add(new String(linea));
				System.out.println(linea);				//Se imprime la palabra.
			}

		}
		catch(FileNotFoundException ex)
		{			//Por si ocurre algun error durante la carga de palabras.
			System.err.println(ex);
		}
		catch(IOException ex)
		{
			System.err.println(ex);
		}
	}
	
	private String eligePalabra()
	{											// esta funcion se utliza para que el servidor seleccione una palabra al azar de la lista 
		int pos, tam;
		tam = lst.size();
		Random generador = new Random();

		do{
			pos = generador.nextInt() % tam;
													// para obtener una pocision aleatoria mayor que cero.
		}while(pos < 0);

		return new String(lst.get(pos));		// retorna una palabra aleatoreamente.
	}
	
	private void ocultar()
	{							//Este metodo se encarga de intercambiar las letras de la palabra por asteriscos para ocultar su contenido y mostrarlo al jugador.
		oculta = palabra.toCharArray();
		for(int i = 0; i < oculta.length; i++)
			oculta[i] = '*';
	}
	
	private boolean adivina(char l){				 //Este metodo se encarga de comparar una letra enviada por el jugador 
		boolean res = false;
		
		for(int i= 0; i < oculta.length; i++)
			if(palabra.toCharArray()[i] == l){		//si la letra coinciden se intercambia el asterisco por la letra dada.
				oculta[i] = l;
				res = true;
			}										// retorna verdadero si la letra es igual y falso si no.
		
		return res;
	}
	
	public void jugar(Scanner entrada, PrintWriter salida){				// en este metodo se encuentra la logica del juego
		
		boolean ganador = false;
		boolean correcto;
		int vidas = 5;										//El servidor le proporciona 5 vidas al jugador.
		String linea, parcial, mensaje;
		char letra;
		
		cargarPalabras();									// se cargan las palabras
		System.out.println("\n");
		
		palabra = eligePalabra();							// El servidor selecciona una palabra que el jugador adivine
		System.out.println("Se eligio la palabra " + palabra);
		
		ocultar();										//Se oculta la palabra
		ganador = false;
		salida.println(new String(oculta));				// se le muestra la palbra oculta con asteriscos al jugador
		salida.flush();
		salida.println("ingrese una letra y presione Enter");
		salida.flush();

		while(!ganador && vidas > 0){					// se utiliza el ciclo while para 					
			linea = entrada.nextLine();						// se recibe la letra que el uugador mando al servidor.
			System.out.println("Se recibio la siguiente Letra del cliente " + linea);
			letra = linea.toCharArray()[0];   
			correcto = adivina(letra);		//se le envia la letra al metodo adivina para saber si el jugador le atino.
			parcial = new String(oculta);   // se actualiza la cadena de asteriscos intercambiando la letra del jugador si este concordo con la de la palabra.
			if(!correcto) vidas--;			//si la letra no coicide con ninguna letra de la palbra se reduce el contador de vidas del jugador.
			else{
				if(palabra.equals(parcial)){  // se verifica si la palbra esta completa si es asi a la variable ganador se le asigna true para despues salir del ciclo while.
					ganador = true;
				}
			}
			mensaje = "vidas = " + vidas + " " + parcial;    //A la variable mensaje se le asigna el estatus del jugador.
			salida.println(mensaje);						//Se envia el mensaje al jugador
			salida.flush();
			System.out.println(mensaje);
			if(ganador || vidas == 0){
				salida.println("termina");
				salida.flush();
				System.out.println("termina");
			}else{
				salida.println("ingrese una letra y presione enter");
				salida.flush();
				System.out.println("sigue");
			}
		}
		
		if(ganador) 
			salida.println("Felicidades ganaste!!!");			//despues de salir del ciclo si la variable ganador es true se le envia al jugador el mensaje de que gano.

		else 
			salida.println("Lastima Perdiste, la palabra era " + palabra); // sino el mensaje de que perdio

		salida.flush();

		System.out.println("Juego terminado");
	}
	
}

