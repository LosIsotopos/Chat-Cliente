package cliente;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import com.google.gson.Gson;

import frames.MiChat;
import frames.VentanaContactos;
import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteDeUsuarios;
import mensajeria.PaqueteMensaje;
import mensajeria.PaqueteUsuario;

/**
 * La clase EscuchaMensajes tiene como función esuchar los mensajes que se
 * enviaran al servidor.
 */
public class EscuchaServer extends Thread {

	private Cliente cliente;
	private ObjectInputStream entrada;
	private final Gson gson = new Gson();
	private MiChat chat;

	protected static ArrayList<String> usuariosConectados = new ArrayList<String>();

	/**
	 * Constructor de EsuchaMensaje
	 * 
	 * @param juego
	 *            juego del que se escucha el mensaje
	 */
	public EscuchaServer(final Cliente cliente) {
		this.cliente = cliente;
		this.entrada = cliente.getEntrada();
	}

	@Override
	public void run() {

		try {

			Paquete paquete;
			PaqueteUsuario paqueteUsuario;
			PaqueteMensaje paqueteMensaje;

			String objetoLeido;
			while (true) {

				synchronized (entrada) {
					objetoLeido = (String) entrada.readObject();	
				}
				paquete = gson.fromJson(objetoLeido, Paquete.class);

				switch (paquete.getComando()) {
				
					case Comando.INICIOSESION:
						usuariosConectados = (ArrayList<String>) gson.fromJson(objetoLeido, PaqueteDeUsuarios.class).getPersonajes();
						break;
						
					// CONEXION = SE CONECTO OTRO USUARIO, ENTONCES LE MANDO LA LISTA
					// A TODOS LOS USUARIOS ANTERIORES A EL
						
					case Comando.CONEXION:
						usuariosConectados = (ArrayList<String>) gson.fromJson(objetoLeido, PaqueteDeUsuarios.class).getPersonajes();
						cliente.getPaqueteUsuario().setListaDeConectados(usuariosConectados);
						actualizarLista(cliente);
						break;
					// ACA RECIBI EL MENSAJE DEL OTRO CLIENTE
					case Comando.TALK:
						
						cliente.setPaqueteMensaje((PaqueteMensaje) gson.fromJson(objetoLeido, PaqueteMensaje.class));
						
//						if(!(chatsActivos.contains(cliente.getPaqueteMensaje().getUserEmisor()) && chatsActivos.contains(cliente.getPaqueteMensaje().getUserReceptor()))) {
							// PARA SACAR EL STATIC LE MANDAMOS LA LISTA AL CHAT
						if(!(cliente.getChatsActivos().containsKey(cliente.getPaqueteMensaje().getUserEmisor()))) {	
//							chat = new MiChat(cliente, chatsActivos);
							chat = new MiChat(cliente);
							
							chat.setTitle(cliente.getPaqueteMensaje().getUserEmisor());
							chat.setVisible(true);
							
							cliente.getChatsActivos().put(cliente.getPaqueteMensaje().getUserEmisor(), chat);
//							cliente.getChatsActivos().put(cliente.getPaqueteMensaje().getUserReceptor());
//							chatsActivos.add(cliente.getPaqueteMensaje().getUserEmisor());
//							chatsActivos.add(cliente.getPaqueteMensaje().getUserReceptor());
//						}
						}
						cliente.getChatsActivos().get(cliente.getPaqueteMensaje().getUserEmisor()).getChat().append(cliente.getPaqueteMensaje().getUserEmisor() + ": "  + cliente.getPaqueteMensaje().getMensaje() + "\n");

//						chat.getChat().append(cliente.getPaqueteMensaje().getUserEmisor() + ": "  + cliente.getPaqueteMensaje().getMensaje() + "\n");
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

	private void actualizarLista(final Cliente cliente) {
		DefaultListModel<String> modelo = new DefaultListModel<String>();
		synchronized (cliente) {
			try {
				cliente.wait(300);
				VentanaContactos.getList().removeAll();
				if (cliente.getPaqueteUsuario().getListaDeConectados() != null) {
					cliente.getPaqueteUsuario().getListaDeConectados().remove(cliente.getPaqueteUsuario().getUsername());
					for (String cad : cliente.getPaqueteUsuario().getListaDeConectados()) {
						modelo.addElement(cad);
					}
					VentanaContactos.getLblNumeroConectados().setText(String.valueOf(modelo.getSize()));
//					VentanaContactos.lblNumeroConectados.setText(String.valueOf(modelo.getSize()));
					VentanaContactos.getList().setModel(modelo);
//					VentanaContactos.list.setModel(modelo);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Pide los usuarios conectados
	 * 
	 * @return devuelve el mapa con los personajes conectados
	 */
//	public Map<String, PaqueteUsuario> getPersonajesConectados() {
//		return personajesConectados;
//	}

	public static ArrayList<String> getUsuariosConectados() {
		return usuariosConectados;
	}
}