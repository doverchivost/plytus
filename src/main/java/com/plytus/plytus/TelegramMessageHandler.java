package com.plytus.plytus;

import com.plytus.plytus.model.Expense;
import com.plytus.plytus.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

@Controller
public class TelegramMessageHandler {

    private static ExpenseService expenseService;

    @Autowired
    public TelegramMessageHandler(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    public static String answer(long chatId, String message) {
        String decimalWithDotPattern = "([0-9]*)\\.([0-9]*)";
        String decimalWithComaPattern = "([0-9]*),([0-9]*)";
        String answer = "";
        switch (message) {
            case "/start":
                answer = "привет";
                break;
            case "/add_expense":
                answer = "Чтобы добавить трату, пришлите боту сообщение в формате:\n" +
                        "   *Название траты*\n" +
                        "   *Цена*\n" +
                        "   *Категория*\n" +
                        "Можно не указывать категорию, тогда она автоматически станет равной названию траты. \n" +
                        "Например:\n" +
                        "   *Карандаш*\n" +
                        "   *13.50*\n" +
                        "   *Канцелярия*";
                break;
            case "/delete_expense":
                answer = "Чтобы удалить трату отправьте боту сообщение со словом «удалить» или «delete» и номером " +
                        "траты. Номер траты можно узнать по команде \n`list of expenses` \n" +
                        "Например: \n" +
                        "   *удалить 1234*";
                break;
            case "/change_category":
                answer = "Чтобы изменить категорию у определённой траты, отправьте боту сообщение со словом " +
                        "«категория» или «category», номером траты и новым названием категории.\n" +
                        "Например:\n" +
                        "   *Категория 634 доставка еды*";
                break;
            case "/list_of_expenses":
                answer = "я выведу список всех трат";
                break;
            case "/list_of_categories":
                answer = "я выведу список категорий трат за месяц";
                break;
            case "/list_with_percentage":
                answer = "я выведу список категорий трат за месяц в процентах";
                break;
            case "/add_from_csv":
                answer = "Файл-пример в формате csv";
                break;
        }

        if (answer.length() > 2) return answer;

        String command = message.split(" ")[0].toLowerCase();
        if (command.equals("удалить") || command.equals("delete")) {
            int expenseIdToDelete = Integer.parseInt(message.split(" ")[1]);
            answer = "БУДЕТ удалена трата с id = " + expenseIdToDelete;
            //return answer;
        }
        else if (command.equals("категория") || command.equals("category")) {
            int expenseId = Integer.parseInt(message.split(" ")[1]);
            String newCategoryName = message.split(" ")[2].toLowerCase();
            //проверить есть ли у юзера такая категория
            //если да не создавая новую изменить внешний ключ на категорию в сущности
            //если нет добавить в Категории новую категорию, изменить саму трату
            answer = "у траты с id = " + expenseId + " будет категория ~" + newCategoryName + "~";
            //return answer;
        }
        else if (message.split("\n").length > 1) {
            if (Character.isDigit(message.split("\n")[1].charAt(0))) {
                String[] msg = message.split("\n");
                String expenseName = msg[0].toLowerCase().trim();
                Date expenseDate = new Date();

                String msgPrice = msg[1].trim();
                double expensePrice = 0;
                if (Pattern.matches(decimalWithDotPattern, msgPrice))
                    expensePrice = Double.parseDouble(msgPrice);
                else if (Pattern.matches(decimalWithComaPattern, msgPrice))
                    expensePrice = Double.parseDouble(msgPrice.replace(",", "."));

                String expenseCategory = "";
                if (msg.length >= 3)
                    expenseCategory = msg[2].toLowerCase();
                else
                    expenseCategory = expenseName;

                Expense expense = new Expense(expenseName, expenseDate, expensePrice);
                Long expenseId = expenseService.saveNewExpense(expense).getId();
                answer = "Добавлена трата с \n" +
                        "id = ~" + expenseId + "~\nname = ~" + expenseName + "~\n" +
                        "price = ~" + expensePrice + "~\ndate = " + expenseDate + "~";
            }
        }
        else {
            answer = "что-то пошло не так :(";
        }

        return answer;

        /*
        //пока без проверки на сообщения, т.е. только добавления трат "вкусное мороженое 15"
        String[] msg = message.split(" ");
        String expenseName = null;
        double expensePrice = 0;



        for (int i = msg.length - 1; i > 0; i--){
            String msgPart = msg[i];
            if (Pattern.matches(decimalWithDotPattern, msgPart)) {
                expensePrice = Double.parseDouble(msgPart);
                String[] name = Arrays.copyOfRange(msg, 0, i);
                expenseName = String.join(" ", name);
                break;
            }
            else if (Pattern.matches(decimalWithComaPattern, msgPart)) {
                expensePrice = Double.parseDouble(msgPart.replace(",", "."));
                String[] name = Arrays.copyOfRange(msg, 0, i);
                expenseName = String.join(" ", name);
                break;
            }
        }
        Expense expense = new Expense(expenseName, expensePrice);
        Long idOfAddedExpense = expenseService.saveNewExpense(expense).getId();
        return "трата добавлена с id = " + idOfAddedExpense +
                "\n трата: ~" + expenseName + "~ \n ее цена: ~" + expensePrice + "~";*/
    }
}
