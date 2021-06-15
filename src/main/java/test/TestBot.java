package test;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import telegramBot.Bot;

public class TestBot {

	public static void main(String[] args) {
		
		
		try {
			TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
			botsApi.registerBot(new Bot());
			
			System.out.println("Bot en linea...");
		
		} catch (TelegramApiException e) {
		
			e.printStackTrace();
		}

	}

}
