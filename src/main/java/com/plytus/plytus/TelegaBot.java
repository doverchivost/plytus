package com.plytus.plytus;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.GetFileResponse;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TelegaBot {
    static TelegramBot bot;
    static final File exampleCSV = new File("src/main/java/example.csv");

    public static void run() {
        String telegram_token = System.getenv("plytus_bot_token");
        bot = new TelegramBot(telegram_token);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                if (dayOfMonth == 1)
                    TelegramMessageHandler.sendMonthCSV();
            }
        }, 0, 24 * 60 * 60 * 1000);

        bot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                long chatId = update.message().chat().id();
                String message = update.message().text();
                String answer = "";
                if (message != null) {
                    message = message.trim();
                    if (message.contains("*")) {
                        answer = "Недопустимый символ \"*\"";
                    }
                    else {
                        answer = TelegramMessageHandler.answer(chatId, message);
                        if (message.equals("/add_from_csv")) {
                            bot.execute(new SendDocument(chatId, exampleCSV).caption("example.csv").fileName("example.csv"));
                        }
                    }
                }
                else if (update.message().document() != null) {
                    Document doc = update.message().document();
                    String ext = doc.fileName().split("\\.")[doc.fileName().split("\\.").length - 1];
                    answer = "ты отправил мне файл с расширением " + ext;
                    if (ext.equalsIgnoreCase("csv")) {
                        //обработать csv файл
                        GetFile request = new GetFile(doc.fileId());
                        GetFileResponse getFileResponse = bot.execute(request);
                        com.pengrad.telegrambot.model.File csvFile = getFileResponse.file();
                        String filePath = csvFile.filePath();

                        try {
                            String fullPath = bot.getFullFilePath(csvFile);
                            InputStream is = new URL(fullPath).openStream();
                            String fileName = "src/main/java/userscsv/user" + chatId + ".csv";
                            Files.copy(is, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);

                            try {
                                answer = TelegramMessageHandler.addExpensesFromSCV(fileName ,chatId);
                            }
                            catch (Exception e) {
                                answer = "Ошибка с файлом :(\n" +
                                        "Убедитесь, что в полях суммы используется точка!";
                            }
                            finally {
                                Files.delete(Paths.get(fileName));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
