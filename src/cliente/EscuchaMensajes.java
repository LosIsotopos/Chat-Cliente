package cliente;

import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteDeUsuarios;
import mensajeria.PaqueteMensaje;
import mensajeria.PaqueteUsuario;
/**La clase EscuchaMensajes tiene como función  
 * esuchar los mensajes que se enviaran
 * al servidor.
 */
public class EscuchaMensajes extends Thread {

	private Cliente cliente;
	private ObjectInputStream entrada;
	private final Gson gson = new Gson();

	private Map<String, PaqueteUsuario> personajesConectados;
	protected static ArrayList<String> usuariosConectados = new ArrayList<String>();

	/**Constructor de EsuchaMensaje
	 * @param juego juego del que se escucha el mensaje
	 */
	public EscuchaMensajes(final Cliente cliente) {
		this.cliente = cliente;
		this.entrada = cliente.getEntrada();
	}

	@Override
	public void run() {

		try {

			Paquete paquete;
			PaqueteUsuario paqueteUsuario;
			PaqueteMensaje paqueteMensaje;
			personajesConectados = new HashMap<>();

			while (true) {

				String objetoLeido = (String) entrada.readObject();

				paquete = gson.fromJson(objetoLeido , Paquete.class);

				switch (paquete.getComando()) {

					case Comando.CONEXION:
						usuariosConectados = gson.fromJson(objetoLeido, PaqueteDeUsuarios.class).getPersonajes();
//						usuariosConectados = gson.fromJson(objetoLeido, ArrayList.class);
						break;
	
					case Comando.TALK:
						paqueteMensaje = gson.fromJson(objetoLeido, PaqueteMensaje.class);
						break;
	
					case Comando.CHATALL:
						paqueteMensaje = gson.fromJson(objetoLeido, PaqueteMensaje.class);
						break;
				}
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Fallo la conexión con el servidor.");
			e.printStackTrace();
		}
	}
	
	/**Pide los personajes conectados
	 * @return devuelve el mapa con los personajes conectados
	 */
	public Map<String, PaqueteUsuario> getPersonajesConectados() {
		return personajesConectados;
	}
	
	public static ArrayList<String> getUsuariosConectados() {
		return usuariosConectados;
	}
}