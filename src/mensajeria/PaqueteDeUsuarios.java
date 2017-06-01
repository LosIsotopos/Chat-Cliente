package mensajeria;

import java.io.Serializable;
import java.util.ArrayList;

public class PaqueteDeUsuarios extends Paquete implements Serializable, Cloneable {

		private ArrayList<String> usuarios;

		public PaqueteDeUsuarios(){

		}

		public PaqueteDeUsuarios(ArrayList<String> usuarios){
			this.usuarios = usuarios;
		}

		public ArrayList<String> getPersonajes(){
			return usuarios;
		}

		@Override
		public Object clone() {
			Object obj = null;
			obj = super.clone();
			return obj;
		}

	}
