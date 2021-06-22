package telegramBot;

public class Dispositivo {
	
	String nombre;

	Double temperatura;
	public Dispositivo(String nombre, Double temperatura) {
		super();
		this.nombre = nombre;

		this.temperatura = temperatura;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Double getTemperatura() {
		return temperatura;
	}
	public void setTemperatura(Double temperatura) {
		this.temperatura = temperatura;
	}
	
	public String toString() {
		
		return nombre + " " + " " + temperatura + "ºC";
		
	}

}
