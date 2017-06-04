package mensajeria;

import java.io.Serializable;

public class PaqueteMensaje extends Paquete implements Serializable, Cloneable {
	
		private int idEmisor;
		private int idReceptor;
		private String mensaje;

		public PaqueteMensaje(int idEmisor, int idReceptor, String mensaje){
			this.idEmisor = idEmisor;
			this.idReceptor = idReceptor;
			this.mensaje = mensaje;
			setComando(Comando.TALK);
		}

		public String getMensaje() {
			return mensaje;
		}

		public void setMensaje(String mensaje) {
			this.mensaje = mensaje;
		}

		public int getIdEmisor() {
			return idEmisor;
		}


		public void setIdEmisor(int idEmisor) {
			this.idEmisor = idEmisor;
		}


		public int getIdReceptor() {
			return idReceptor;
		}

		public void setIdReceptor(int idReceptor){
			this.idReceptor = idReceptor;
		}

}
