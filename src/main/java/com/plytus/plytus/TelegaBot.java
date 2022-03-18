package com.plytus.plytus;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.*;

public class TelegaBot {
    static TelegramBot bot;
    static final File exampleCSV = new File("src/main/java/example.csv");

    public static void run() {
        String telegram_token = System.getenv("plytus_bot_token");
        bot = new TelegramBot(telegram_token);

        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                long chatId = update.message().chat().id();
                String message = update.message().text();
                String answer = "";
                if (message != null) {
                    message = message.trim();
                    answer = TelegramMessageHandler.answer(chatId, message);
                    if (message.equals("/add_from_csv")) {
                        try {
                            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(exampleCSV));
                            byte[] file = bis.readAllBytes();
                            bot.execute(new SendDocument(chatId, file).caption("example.csv").fileName("example.csv"));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if (update.message().document() != null) {
                    Document doc = update.message().document();
                    String ext = doc.fileName().split("\\.")[doc.fileName().split("\\.").length - 1];
                    answer = "ты отправил мне файл с расширением " + ext;
                    if (ext.equalsIgnoreCase("csv")) {
                        //обработать csv файл
                    }
                }
                else {
                    answer = "Ты отправил мне что-то не то :(";
                }
                bot.execute(new SendMessage(chatId, answer).parseMode(ParseMode.Markdown));
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
