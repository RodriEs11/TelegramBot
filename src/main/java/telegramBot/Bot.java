package telegramBot;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updates.GetUpdates;
import org.telegram.telegrambots.meta.api.methods.updates.GetWebhookInfo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

	public void onUpdateReceived(Update update) {

		
		try {
			String json = Json.extractContent("https://api2.nicehash.com/main/api/v2/mining/external/3HRDzdG1Xiu9d6Q8TL7fjGYz3yQ1cdH9es/rigs2");

			JSONObject datosJson = new JSONObject(json);
			
			ArrayList<Rig> listaRigs = this.parsearRigs(datosJson);
			
			for(Rig rig: listaRigs) {
				
				this.enviarMensaje(update, rig.nombre + ": " + rig.estado + "\n" + rig.getDispositivos());
				
			}
		
			
			

		} catch (MalformedURLException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}

	
	public ArrayList<Rig> parsearRigs(JSONObject json) {
		
		JSONArray miningRigs = json.getJSONArray("miningRigs");

		int totalRigs = json.getInt("totalRigs");
		
		ArrayList<Rig> listaRigs = new ArrayList<Rig>();
		
		
		for(int i = 0; i< totalRigs; i++) {
			
			
			JSONObject rig = miningRigs.getJSONObject(i);
			
			String nombreRig = rig.getString("name");
			String estadoRig = rig.getString("minerStatus");
			
			if(estadoRig.contentEquals("MINING")) {
				estadoRig = "Minando";
			}else {
				
				estadoRig = "Detenido";
			}
			
			Rig nuevoRig = new Rig(nombreRig, estadoRig);
			nuevoRig.setDispositivos(this.parsearDispositivos(rig));		
			listaRigs.add(nuevoRig);
			
			
		}
		
		return listaRigs;
		
	}
	
	
	//PARSEA LOS DISPOSITIVOS ASOCIADOS AL RIG, AGREGA SOLO LOS QUE ESTAN ENCENDIDOS
	
	public ArrayList<Dispositivo> parsearDispositivos(JSONObject rig){
		
		ArrayList<Dispositivo> listaDispositivos = new ArrayList<Dispositivo>();
		
		JSONArray dispositivos = rig.getJSONArray("devices");
		
		for(int j=0; j< dispositivos.length(); j++) {

			JSONObject dispositivo = dispositivos.getJSONObject(j);

			String nombreDevice = dispositivo.getString("name");
			Double temperaturaDevice = (Double) dispositivo.getNumber("temperature");
			Dispositivo dispositivoNuevo = new Dispositivo(nombreDevice, temperaturaDevice);
			
			if(temperaturaDevice > 0) {
				listaDispositivos.add(dispositivoNuevo);
			}
			
			
		}
		
		
		return listaDispositivos;
		
	}
	
	public void enviarMensaje(Update update, String mensaje) {

		SendMessage enviar = new SendMessage();
		enviar.setChatId(update.getMessage().getChatId().toString());
		enviar.setText(mensaje);

		try {
			execute(enviar);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void enviarMensaje(Update update, int mensaje) {

		SendMessage enviar = new SendMessage();
		enviar.setChatId(update.getMessage().getChatId().toString());

		enviar.setText(String.valueOf(mensaje));

		try {
			execute(enviar);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String getChatId(Update update) {

		return update.getMessage().getChatId().toString();

	}

	public String getBotUsername() {
		return Datos.BOT_USERNAME;
	}

	@Override
	public String getBotToken() {
		return Datos.BOT_TOKEN;
	}

}
