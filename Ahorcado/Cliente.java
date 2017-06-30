import java.io.*;
import java.net.*;
import java.util.*;

public class Cliente 
{
    private static Socket s = null;	
    												// se declara una variable socket
	public static void main(String[] args)
	 {
		
		try 
		{
			s = new Socket("localhost", 1390);		// se inicializa la variable socket con el parametro localhost porque el servidor y los clientes se ejecutaran en la misma maquina.
			System.out.println("Se conecto con el servidor");
			String linea, usuario, comando;					 //variables tipo strings
			PrintWriter salida = null;
			Scanner		entrada = null;							//variables para comunicarse entre servidor y cliente
			Scanner		teclado = new Scanner(System.in);
			
			salida = new PrintWriter(s.getOutputStream());		// se inicializan las comunicaciones entrada y salida.
			entrada = new Scanner(s.getInputStream());
			boolean terminar = false;

			while(!terminar)
			{
				linea = entrada.nextLine();				//Recibe un mensaje del servidor y lo imprime
				System.out.println(linea);
				comando = entrada.nextLine();
				System.out.println(comando);
				if(comando.trim().toUpperCase().startsWith("TERMINA")) 
				{   													// verifica Si lo que contiene comando es igual a termina
		 		    terminar = true;
		 		}
				if (!terminar)
				{               					//si terminar es falso entonces el jugador continua jugando.
					System.out.print(">>");
					usuario = teclado.nextLine();  //a la variable usuario se le asigna lo leido desde teclado.
					salida.println(usuario);		// se le envia al servidor la respuesta del jugador.
					salida.flush();
				}
			}
			linea = entrada.nextLine();			//A la variable linea se la asigna lo enviado por el servidor
			System.out.println(linea);			// se imprime su contenido.
			
		} 
		catch (UnknownHostException e) 
		{  										// Si hubo un error 
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		try 
		{
 	    	s.close();
 		} 
 		catch (java.io.IOException eio) 
 		{ 
 	   		System.err.println(eio);
 		}
	}

}
