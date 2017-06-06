package cliente;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JOptionPane;

import com.google.gson.Gson;

import mensajeria.Comando;
import mensajeria.Paquete;
import mensajeria.PaqueteMensaje;
import mensajeria.PaqueteUsuario;

public class Cliente extends Thread {
	private Socket cliente;
	private static String miIp;
	private ObjectInputStream entrada;
	private ObjectOutputStream salida;
	private PaqueteUsuario paqueteUsuario = new PaqueteUsuario();
	private PaqueteMensaje paqueteMensaje = new PaqueteMensaje();

	private int accion;
	
	private final Gson gson = new Gson();
	
	private String ip;
	private int puerto;
	
	public Cliente() {
		Scanner sc;
	
		try {
			sc = new Scanner(new File("config.txt"));
			ip = sc.nextLine();
			puerto = sc.nextInt();
			sc.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "No se ha encontrado el "
					+ "archivo de configuración config.txt");
			e.printStackTrace();
		}
	
		try {
			cliente = new Socket(ip, puerto);
			miIp = cliente.getInetAddress().getHostAddress();
			entrada = new ObjectInputStream(cliente.getInputStream());
			salida = new ObjectOutputStream(cliente.getOutputStream());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Fallo al iniciar la aplicación. "
					+ "Revise la conexión con el servidor.");
			System.exit(1);
			e.printStackTrace();
		}
	}


	@Override
	public void run() {
		synchronized(this) {
			try {
				// Creo el paquete que le voy a enviar al servidor
				paqueteUsuario = new PaqueteUsuario();
				while (!paqueteUsuario.isInicioSesion()) {
					
					// Espero a que el usuario seleccione alguna accion
					wait();
					
					switch (getAccion()) {
					
						case Comando.INICIOSESION:
							paqueteUsuario.setComando(Comando.INICIOSESION);
							// Le envio el paquete al servidor
							salida.writeObject(gson.toJson(paqueteUsuario));
							break;
							
						case Comando.TALK:
							paqueteMensaje.setComando(Comando.TALK);
							
							// Le envio el paquete al servidor
							salida.writeObject(gson.toJson(paqueteMensaje));
							
							System.out.println("Envie mensaje");
//							paqueteMensaje = gson.fromJson(objetoLeido, PaqueteMensaje.class);
							break;
							
						case Comando.CHATALL:
//							paqueteMensaje = gson.fromJson(objetoLeido, PaqueteMensaje.class);
							break;
							
						case Comando.SALIR:
							paqueteUsuario.setIp(getMiIp());
							paqueteUsuario.setComando(Comando.SALIR);
							// Le envio el paquete al servidor
							salida.writeObject(gson.toJson(paqueteUsuario));
							break;
							
						default:
							break;
					}
			
					// Le envio el paquete al servidor
					/**
					 * ESTA ARRIBA EN CADA SWITCH PORQUE USO DOS PAQUETES DIFERENTES  
					*/
//					salida.writeObject(gson.toJson(paqueteUsuario));

					// Recibo el paquete desde el servidor
					String cadenaLeida = (String) entrada.readObject();
					Paquete paquete = gson.fromJson(cadenaLeida, Paquete.class);
			
					switch (paquete.getComando()) {
			
						case Comando.INICIOSESION:
							if (paquete.getMensaje().equals(Paquete.msjExito)) {
				
								// El usuario ya inicio sesión
								paqueteUsuario.setInicioSesion(true);
								
								// Recibo el paquete personaje con los datos
								this.paqueteUsuario = gson.fromJson(cadenaLeida, PaqueteUsuario.class);
				
							} else {
								if (paquete.getMensaje().equals(Paquete.msjFracaso)) {
									JOptionPane.showMessageDialog(null, "El usuario ya se encuentra logeado.");
								}
								// El usuario no pudo iniciar sesión
								paqueteUsuario.setInicioSesion(false);
							}
							break;
				
						case Comando.SALIR:
							// El usuario no pudo iniciar sesión
							paqueteUsuario.setInicioSesion(false);
							salida.writeObject(gson.toJson(new Paquete(Comando.DESCONECTAR), Paquete.class));
							cliente.close();
							break;
							
						case Comando.DESCONECTAR:
							PaqueteUsuario pU = new PaqueteUsuario();
							pU.setComando(Comando.DESCONECTAR);
							salida.writeObject(gson.toJson(pU, PaqueteUsuario.class));
							break;
							
						case Comando.TALK:
							System.out.println("NOPE CLIENTE AMIGUITO");
//							paqueteMensaje = gson.fromJson(objetoLeido, PaqueteMensaje.class);
							break;
							
						case Comando.CHATALL:
//							paqueteMensaje = gson.fromJson(objetoLeido, PaqueteMensaje.class);
							break;
							
						default:
							break;
						}
//					wait();
				}
			
				// Establezco el mapa en el paquete usuario
				paqueteUsuario.setIp(miIp);
				salida.writeObject(gson.toJson(paqueteUsuario));
				notify();
				
			} catch (IOException | InterruptedException | ClassNotFoundException e) {
				JOptionPane.showMessageDialog(null, "Fallo la conexión con el servidor durante el inicio de sesión.");
				System.exit(1);
				e.printStackTrace();
			}
		}
	}

	public void setAccion(int accion) {
		this.accion = accion;
	}
	
	public int getAccion() {
		return accion;
	}
	
	public Socket getSocket() {
		return cliente;
	}

	public void setSocket(final Socket cliente) {
		this.cliente = cliente;
	}

	public static String getMiIp() {
		return miIp;
	}

	public void setMiIp(final String miIp) {
		this.miIp = miIp;
	}

	public ObjectInputStream getEntrada() {
		return entrada;
	}
	
	public void setEntrada(final ObjectInputStream entrada) {
		this.entrada = entrada;
	}
	
	public ObjectOutputStream getSalida() {
		return salida;
	}
	
	public void setSalida(final ObjectOutputStream salida) {
		this.salida = salida;
	}

	public PaqueteUsuario getPaqueteUsuario() {
		return paqueteUsuario;
	}
	
	public PaqueteMensaje getPaqueteMensaje() {
		return paqueteMensaje;
	}

	public void setPaqueteMensaje(PaqueteMensaje fromJson) {
		this.paqueteMensaje.setMensaje(fromJson.getMensaje());
		this.paqueteMensaje.setUserEmisor(fromJson.getUserEmisor());
		this.paqueteMensaje.setUserReceptor(fromJson.getUserReceptor());
	}

}