package com.plytus.plytus;

import com.plytus.plytus.model.Expense;
import com.plytus.plytus.services.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Arrays;
import java.util.regex.Pattern;

@Controller
public class TelegramMessageHandler {

    private static ExpenseService expenseService;

    @Autowired
    public TelegramMessageHandler(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    public static String answer(long chatId, String message) {
        if (message.equals("/start")) return "привет";

        //пока без проверки на сообщения, т.е. только добавления трат "вкусное мороженое 15"
        String[] msg = message.split(" ");
        String expenseName = null;
        double expensePrice = 0;

        String decimalWithDotPattern = "([0-9]*)\\.([0-9]*)";
        String decimalWithComaPattern = "([0-9]*),([0-9]*)";

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
        //хз как правильно вызвать не статик в статик
        //new ExpenseController().addExpense(expense);
        //Long idOfAddedExpense = expenseService.saveNewExpense(expense).getId();
        //expenseService.saveNewExpense(expense);
        return "трата БУДЕТ добавлена с id =  (пока нет)" +
                "\n трата: ~" + expenseName + "~ \n ее цена: ~" + expensePrice + "~";
    }
}
