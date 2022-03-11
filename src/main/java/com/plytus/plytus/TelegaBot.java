package com.plytus.plytus;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

public class TelegaBot {
    static TelegramBot bot;

    public static void run() {
        String telegram_token = System.getenv("plytus_bot_token");
        bot = new TelegramBot(telegram_token);
        //TelegramMessageHandler handler = new TelegramMessageHandler();

        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                long chatId = update.message().chat().id();
                String message = update.message().text();
                String answer = TelegramMessageHandler.answer(chatId, message);
                SendResponse response = bot.execute(new SendMessage(chatId, answer));
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
