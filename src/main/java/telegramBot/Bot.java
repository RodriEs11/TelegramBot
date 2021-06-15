package telegramBot;


import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

	public void onUpdateReceived(Update update) {

		if (update.getMessage().getText().contains("/help")) {
			
			

			SendMessage opciones = new SendMessage();
			opciones.setChatId(this.getChatId(update));

			String mensajeOpciones = "/help: muestra los comandos disponibles \n/foto: el bot envia una foto de internet";
			opciones.setText(mensajeOpciones);

			try {

				execute(opciones);

			} catch (TelegramApiException e) {
				e.printStackTrace();

			}

		} 

		if (update.getMessage().getText().contains("/foto")) {

			SendPhoto foto1 = new SendPhoto();
			foto1.setChatId(this.getChatId(update));
			foto1.setPhoto(new InputFile("https://i.pinimg.com/564x/c4/76/27/c476278504682e622fabe9b0932098c3.jpg"));

			try {

				execute(foto1);

			} catch (TelegramApiException e) {
				e.printStackTrace();
			}

		} 
		
		if(update.getMessage().hasText() && !update.getMessage().getText().contains("/foto") && !update.getMessage().getText().contains("/help")) {
			
			SendMessage mensajeAviso = new SendMessage();
			mensajeAviso.setChatId(this.getChatId(update));
			mensajeAviso.setText("Yo repito todo lo que decis");
			
			SendMessage mensajeRepetido = new SendMessage();
			mensajeRepetido.setChatId(this.getChatId(update));
			mensajeRepetido.setText(update.getMessage().getText());

			try {
				
				execute(mensajeAviso);
				execute(mensajeRepetido);
				
			} catch (TelegramApiException e) {

				e.printStackTrace();
			}
		}

		

	}

	public String getChatId(Update update) {

		return update.getMessage().getChatId().toString();

	}

	public String getBotUsername() {
		return "mi bot";
	}

	@Override
	public String getBotToken() {
		return "<token>";
	}

}
