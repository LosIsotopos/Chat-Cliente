package mensajeria;

import java.io.Serializable;

public class PaqueteUsuario extends Paquete implements Serializable, Cloneable {

	private String username;
	private boolean inicioSesion;
	private boolean estado;

	public PaqueteUsuario(){
		
	}

	public PaqueteUsuario(String user){
		username = user;
		inicioSesion = false;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public boolean isInicioSesion() {
		return inicioSesion;
	}

	public void setInicioSesion(boolean inicioSesion) {
		this.inicioSesion = inicioSesion;
	}
	
	public boolean getEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	
	public Object clone() {
		Object obj = null;
		obj = super.clone();
		return obj;
	}
	
	
}
