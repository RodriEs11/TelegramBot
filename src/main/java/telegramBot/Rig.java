package telegramBot;

import java.util.ArrayList;

public class Rig {
	
	String nombre;
	String estado;
	ArrayList<Dispositivo> dispositivos = new ArrayList<Dispositivo>();
	
	
	public Rig(String nombre, String estado) {
		super();
		this.nombre = nombre;
		this.estado = estado;
	
	}


	public String getNombre() {
		return nombre;
	}


	public void setNombre(String nombre) {
		this.nombre = nombre;
	}


	public String getEstado() {
		return estado;
	}


	public void setEstado(String estado) {
		this.estado = estado;
	}



	public ArrayList<Dispositivo> getDispositivos() {
		return dispositivos;
	}


	public void setDispositivos(ArrayList<Dispositivo> dispositivos) {
		this.dispositivos = dispositivos;
	}


	public String toString() {
		
		return nombre + " estado: " + estado;
	}
	

}
