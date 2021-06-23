package telegramBot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import org.telegram.telegrambots.meta.api.methods.GetFile;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;

import org.telegram.telegrambots.meta.api.objects.InputFile;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

	public void onUpdateReceived(Update update) {

		this.enviarMensaje(update, "hola");

	

		try {
			String json = Json.extractContent(
					"https://api2.nicehash.com/main/api/v2/mining/external/3HRDzdG1Xiu9d6Q8TL7fjGYz3yQ1cdH9es/rigs2");
			String precios = Json.extractContent("https://api2.nicehash.com/exchange/api/v2/info/prices");

			JSONObject datosJson = new JSONObject(json);
			JSONObject preciosJson = new JSONObject(precios);

			DecimalFormat formatoDouble = new DecimalFormat("0.00");

			Double precioBTC = (Double) preciosJson.getNumber("BTCUSDC");
			Double minado = (Double) datosJson.getNumber("unpaidAmount");
			Double minadoUSD = precioBTC * minado;
			Double profitActual = (Double) datosJson.getNumber("totalProfitability") * precioBTC;

			ArrayList<Rig> listaRigs = this.parsearRigs(datosJson);

			this.enviarMensaje(update, "Rigs totales: " + datosJson.getInt("totalRigs"));
			this.enviarMensaje(update, "BTC address: " + datosJson.getString("btcAddress"));
			this.enviarMensaje(update, "Precio BTC: $" + formatoDouble.format(precioBTC) + " USD");
			this.enviarMensaje(update, "Saldo minado: $" + formatoDouble.format(minadoUSD) + " USD");
			this.enviarMensaje(update, "Rentabilidad: $" + formatoDouble.format(profitActual) + " USD / 24h");

			String hora = datosJson.getString("nextPayoutTimestamp");
			Instant proximoPago = Instant.parse(hora);

			this.enviarMensaje(update,
					"Proximo pago en: " + this.tiempoRestante(proximoPago).getHour() + "h "
							+ this.tiempoRestante(proximoPago).getMinute() + "m "
							+ this.tiempoRestante(proximoPago).getSecond() + "s");

			for (Rig rig : listaRigs) {

				this.enviarMensaje(update, rig.nombre + ": " + rig.estado + "\n" + rig.getDispositivos());

			}

			System.out.println("Mensaje recibido a las: " + LocalTime.now());

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} catch (Exception e) {
			e.printStackTrace();
			this.enviarMensaje(update, "Estoy ocupado, no me rompas las pelotas");
			this.enviarSticker(update, "E:/Mis imágenes/Stickers/ahre.png");
			System.out.println("Mensaje recibido a las: " + LocalTime.now());
		}

	}

	public LocalTime tiempoRestante(Instant proximoPago) {

		// PARSEA EL JSON A LA HORA DE BUENOS AIRES
		ZonedDateTime buenosAires = proximoPago.atZone(ZoneId.of("America/Argentina/Buenos_Aires"));

		LocalTime horaActual = LocalTime.now();
		LocalTime horaHasta = buenosAires.toLocalTime();

		LocalTime tiempoRestante = horaHasta.minusSeconds(horaActual.toSecondOfDay());

		return tiempoRestante;

	}

	public ArrayList<Rig> parsearRigs(JSONObject json) {

		JSONArray miningRigs = json.getJSONArray("miningRigs");

		int totalRigs = json.getInt("totalRigs");

		ArrayList<Rig> listaRigs = new ArrayList<Rig>();

		for (int i = 0; i < totalRigs; i++) {

			JSONObject rig = miningRigs.getJSONObject(i);

			String nombreRig = rig.getString("name");
			String estadoRig = rig.getString("minerStatus");

			if (estadoRig.contentEquals("MINING")) {
				estadoRig = "Minando";
			} else {

				estadoRig = "**Detenido**";
			}

			Rig nuevoRig = new Rig(nombreRig, estadoRig);
			nuevoRig.setDispositivos(this.parsearDispositivos(rig));
			listaRigs.add(nuevoRig);

		}

		return listaRigs;

	}

	// PARSEA LOS DISPOSITIVOS ASOCIADOS AL RIG, AGREGA SOLO LOS QUE ESTAN
	// ENCENDIDOS

	public ArrayList<Dispositivo> parsearDispositivos(JSONObject rig) {

		ArrayList<Dispositivo> listaDispositivos = new ArrayList<Dispositivo>();

		JSONArray dispositivos = rig.getJSONArray("devices");

		for (int j = 0; j < dispositivos.length(); j++) {

			JSONObject dispositivo = dispositivos.getJSONObject(j);

			String nombreDevice = dispositivo.getString("name");
			Double temperaturaDevice = (Double) dispositivo.getNumber("temperature");
			Dispositivo dispositivoNuevo = new Dispositivo(nombreDevice, temperaturaDevice);

			if (temperaturaDevice > 0) {
				listaDispositivos.add(dispositivoNuevo);
			}

		}

		return listaDispositivos;

	}

	public void recibirFotoYDescargar(Update update) throws Exception {

		GetFile getFile = new GetFile();
		// getPhoto RETORNA 4 ARCHIVOS, 3 ES ES MAS GRANDE
		getFile.setFileId(update.getMessage().getPhoto().get(3).getFileId());

		org.telegram.telegrambots.meta.api.objects.File fotoRecibida = execute(getFile);
		this.descargarFoto(fotoRecibida.getFileUrl(Datos.BOT_TOKEN));

		System.out.println("Foto descargada en E:/Descargas/");

	}

	public void descargarFoto(String url) {

		try {
			URL ruta = new URL(url);
			URLConnection urlCon = ruta.openConnection();
			// acceso al contenido web
			InputStream is = urlCon.getInputStream();

			// Fichero en el que queremos guardar el contenido
			FileOutputStream fos = new FileOutputStream("E:/Descargas/Telegram" + LocalTime.now().getHour()
					+ LocalTime.now().getMinute() + LocalTime.now().getSecond() + ".jpg");

			// buffer para ir leyendo.
			byte[] array = new byte[1000];

			// Primera lectura y bucle hasta el final
			int leido = is.read(array);
			while (leido > 0) {
				fos.write(array, 0, leido);
				leido = is.read(array);
			}

			// Cierre de conexion y fichero.
			is.close();
			fos.close();

		} catch (Exception e) {

		}

	}

	public void enviarTeclado(Update update) {

		KeyboardButton boton1 = new KeyboardButton();
		boton1.setText("Localizacion");
		boton1.setRequestLocation(true);

		KeyboardButton boton2 = new KeyboardButton();
		boton2.setText("boton2");

		KeyboardRow fila1 = new KeyboardRow();

		fila1.add(boton1);

		KeyboardRow fila2 = new KeyboardRow();

		fila2.add(boton2);

		List<KeyboardRow> filasTeclado = new ArrayList<KeyboardRow>();

		filasTeclado.add(fila1);
		filasTeclado.add(fila2);

		ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
		replyMarkup.setKeyboard(filasTeclado);

		SendMessage enviarTeclado = new SendMessage();
		enviarTeclado.setReplyMarkup(replyMarkup);
		enviarTeclado.setChatId(this.getChatId(update));
		enviarTeclado.setText("Teclado");

		try {
			execute(enviarTeclado);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void enviarFoto(Update update, String ruta) {

		InputFile foto = new InputFile();
		File archivo = new File(ruta);

		foto.setMedia(archivo);

		SendPhoto enviarFoto = new SendPhoto();
		enviarFoto.setChatId(this.getChatId(update));
		enviarFoto.setPhoto(foto);

		try {
			execute(enviarFoto);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void enviarSticker(Update update, String ruta) {

		InputFile sticker = new InputFile();
		File archivo = new File(ruta);

		sticker.setMedia(archivo);

		SendSticker enviarSticker = new SendSticker();
		enviarSticker.setChatId(this.getChatId(update));
		enviarSticker.setSticker(sticker);

		try {
			execute(enviarSticker);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
