package com.plytus.plytus;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendDocument;
import com.plytus.plytus.model.Category;
import com.plytus.plytus.model.Expense;
import com.plytus.plytus.model.User;
import com.plytus.plytus.services.CategoryService;
import com.plytus.plytus.services.ExpenseService;
import com.plytus.plytus.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Controller
public class TelegramMessageHandler {

    private static ExpenseService expenseService;
    private static UserService userService;
    private static CategoryService categoryService;

    static String decimalWithDotPattern = "([0-9]*)\\.([0-9]*)";
    static String decimalWithComaPattern = "([0-9]*),([0-9]*)";
    static String decimalInteger = "([0-9]*)";

    @Autowired
    public TelegramMessageHandler(ExpenseService expenseService, UserService userService, CategoryService categoryService) {
        this.expenseService = expenseService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    public static String answer(long chatId, String message) {
        User expenseUser = checkUser(chatId);



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
                //answer = "я выведу список всех трат";
                answer = monthExpenseMessage(getAllUserExpensesForCurrentMonth(expenseUser));
                break;
            case "/list_of_categories":
                //answer = "я выведу список категорий трат за месяц";
                answer = monthCategoryMessage(getAllUserExpensesForCurrentMonth(expenseUser));
                break;
            case "/list_with_percentage":
                //answer = "я выведу список категорий трат за месяц в процентах";
                answer = monthCategoryPercentMessage(getAllUserExpensesForCurrentMonth(expenseUser));
                break;
            case "/add_from_csv":
                answer = "Файл-пример в формате csv\n" +
                        "Первая строка файла (названия колонок) будет игнорироваться";
                break;
        }

        if (answer.length() > 2) return answer;

        String command = message.split(" ")[0].toLowerCase();
        if (command.equals("удалить") || command.equals("delete")) {
            try {
                long expenseIdToDelete = Long.parseLong(message.split(" ")[1]);
                boolean expenseExist = checkExpenseExist(expenseIdToDelete);
                if (expenseExist) {
                    Expense expenseToDelete = expenseService.getExpenseById(expenseIdToDelete);
                    if (expenseToDelete.getOwner().getId() == expenseUser.getId()) {
                        //expenseUser.getExpenses().remove(expenseToDelete);
                        //expenseToDelete.getCategory().getExpenses().remove(expenseToDelete);
                        expenseService.deleteExpense(expenseToDelete);
                        answer = "Удалена трата с id = " + expenseIdToDelete;
                    } else {
                        answer = "Это не ваша трата! Вы не можете ее удалить.";
                    }
                } else {
                    answer = "Такой траты не существует!";
                }
            } catch (NumberFormatException e) {
                answer = "id должен быть цифрой";
            }
        }
        else if (command.equals("категория") || command.equals("category")) {
            String[] msg = message.split(" ");
            try {
                long expenseId = Long.parseLong(msg[1]);
                if (msg.length > 2) {
                    String[] name = Arrays.copyOfRange(msg, 2, msg.length);
                    String newCategoryName = String.join(" ", name);
                    Category newCategory = checkCategory(expenseUser, newCategoryName);

                    boolean expenseExist = checkExpenseExist(expenseId);
                    if (expenseExist) {
                        Expense expense = expenseService.getExpenseById(expenseId);
                        if (expense.getOwner().getId() == expenseUser.getId()) {
                            Expense expenseToChange = expenseService.getExpenseById(expenseId);
                            expenseToChange.setCategory(newCategory);
                            expenseService.saveNewExpense(expenseToChange);
                            answer = "У траты с id = ~" + expenseToChange.getId() + "~ теперь \n" +
                                    "категория = ~" + expenseToChange.getCategory().getName() + "~";
                        } else {
                            answer = "Это не ваша трата! Вы не можете менять ее категорию.";
                        }
                    } else {
                        answer = "Такой траты не существует!";
                    }
                } else {
                    answer = "Неправильная команда";
                }
            } catch (NumberFormatException e) {
                answer = "id должен быть цифрой";
            }
        }
        else if (message.split("\n").length > 1) {
            if (Character.isDigit(message.split("\n")[1].charAt(0))) {
                String[] msg = message.split("\n");
                String expenseName = msg[0].toLowerCase().trim();
                Date expenseDate = new Date();

                String msgPrice = msg[1].trim();
                double expensePrice = priceFromString(msgPrice);

                String categoryName = "";
                if (msg.length >= 3)
                    categoryName = msg[2].toLowerCase().trim();
                else
                    categoryName = expenseName;

                Category expenseCategory = checkCategory(expenseUser, categoryName);

                Expense expense = new Expense(expenseName, expenseDate, expensePrice, expenseCategory, expenseUser);
                Long expenseId = expenseService.saveNewExpense(expense).getId();
                answer = "Добавлена трата с \n" +
                        "id = ~" + expenseId + "~\n" +
                        "названием = ~" + expenseName + "~\n" +
                        "ценой = ~" + expensePrice + "~\n" +
                        "датой = ~" + expenseDate + "~\n" +
                        "категорией = ~" + expenseCategory.getName() + "~\n" +
                        "пользователем = ~" + expenseUser.getTg_id() + "~";
            }

            else {
                answer = "Неверные параметры: цена";
            }
        }
        else {
            answer = "что-то пошло не так :(";
        }

        return answer;
    }

    public static String addExpensesFromSCV(String fileName, long chatId) {
        User expenseUser = checkUser(chatId);

        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            List<String[]> lines = reader.readAll();
            String[] firstRow = lines.get(0)[0].split(";");
            if (firstRow[0].contains("название") && firstRow[1].contains("категория") &&
                    firstRow[2].contains("цена") && firstRow[3].contains("дата")) {
                for (int i = 1; i < lines.size(); i++) {
                    String[] row = lines.get(i)[0].split(";");
                    String expenseName = row[0].toLowerCase();
                    String expCategory = row[1].toLowerCase();
                    double expensePrice = priceFromString(row[2]);
                    DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
                    Date expenseDate = format.parse(row[3]);

                    Category expenseCategory = checkCategory(expenseUser, expCategory);
                    Expense expense = new Expense(expenseName, expenseDate, expensePrice, expenseCategory, expenseUser);
                    Long expenseId = expenseService.saveNewExpense(expense).getId();
                }
                return "Траты из csv файла добавлены";
            }
            else {
                return "Неверный csv-файл";
            }
        } catch (ParseException | IOException | CsvException e) {
            e.printStackTrace();
        }
        return "Что-то пошло не так :(";
    }

    private static User checkUser(long id) {
        long dbId = userService.userExists(id);
        if (dbId < 0) {
            User newUser = new User(id);
            return userService.saveNewUser(newUser);
        }
        else {
            return userService.getUserById(dbId);
        }
    }

    private static Category checkCategory(User user, String categoryName) {
        Set<Category> userCategories = user.getCategories();
        if (userCategories != null) {
            for (Category userCategory : userCategories) {
                String name = userCategory.getName();
                if (name.equals(categoryName))
                    return userCategory;
            }
        }
        Category newCategory = new Category(categoryName, user);
        categoryService.saveNewCategory(newCategory);
        return newCategory;
    }

    private static boolean checkExpenseExist(long id) {
        List<Expense> allExpenses = expenseService.getExpenses();
        if (allExpenses != null) {
            for (Expense expense : allExpenses)
                if (expense.getId() == id) return true;
        }
        return false;
    }

    private static double priceFromString(String string) {
        double expensePrice = 0.;
        if (Pattern.matches(decimalWithDotPattern, string) || Pattern.matches(decimalInteger, string))
            expensePrice = Double.parseDouble(string);
        else if (Pattern.matches(decimalWithComaPattern, string))
            expensePrice = Double.parseDouble(string.replace(",", "."));
        return expensePrice;
    }

    private static Set<Expense> getAllUserExpensesForCurrentMonth(User user) {
        Set<Expense> allExpenses = user.getExpenses();
        Set<Expense> monthExpenses = new HashSet<>();

        Calendar calendar = Calendar.getInstance();
        //calendar.add(Calendar.MONTH, - 1);
        //calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        Date currentMonth = calendar.getTime();

        for (Expense expense : allExpenses) {
            if (expense.getDate().after(currentMonth)) {
                monthExpenses.add(expense);
            }
        }
        return monthExpenses;
    }

    private static String monthExpenseMessage(Set<Expense> expenseSet) {
        String answer = "id) название (категория) : сумма\n\n";
        double priceTotal = 0;
        for (Expense expense : expenseSet) {
            answer += String.format("%1$s) %2$s (%3$s) : %4$.2f\n",
                    expense.getId(), expense.getName(), expense.getCategory().getName(), expense.getPrice());
            priceTotal += expense.getPrice();
        }
        answer += String.format("\nСумма всех трат за месяц: %.2f", priceTotal);
        return answer;
    }

    private static String monthCategoryMessage(Set<Expense> expenseSet) {
        String answer = "категория - сумма\n\n";
        Map<String, Double> categoryPrice = getCategoryPriceMap(expenseSet);
        double priceTotal = 0;
        Set<String> keys = categoryPrice.keySet();
        for (String key : keys) {
            double price = categoryPrice.get(key);
            answer += String.format("%s - %.2f\n", key, price);
            priceTotal += price;
        }
        answer += String.format("\nСумма всех трат за месяц: %.2f", priceTotal);
        return answer;
    }

    private static String monthCategoryPercentMessage(Set<Expense> expenseSet) {
        String answer = "% категория (сумма)\n\n";
        Map<String, Double> categoryPrice = getCategoryPriceMap(expenseSet);
        double priceTotal = 0;
        for(Expense expense : expenseSet)
            priceTotal += expense.getPrice();

        Map<String, Double> sortedByPrice = sort(categoryPrice, false);
        for (String category : sortedByPrice.keySet()) {
            double price = sortedByPrice.get(category);
            double percentPrice = price / priceTotal * 100;
            //answer += percentPrice + "% " + category + " (" + price + ")\n";
            answer += String.format("%.2f%% %s (%.2f)\n", percentPrice, category, price);
        }

        answer += String.format("\nСумма всех трат за месяц: %.2f", priceTotal);
        return answer;
    }

    private static Map<String, Double> getCategoryPriceMap (Set<Expense> expenseSet) {
        Map<String, Double> categoryPrice = new HashMap<String, Double>();
        for (Expense expense : expenseSet) {
            String expenseCategory = expense.getCategory().getName();
            if (categoryPrice.containsKey(expenseCategory)) {
                double price = expense.getPrice() + categoryPrice.get(expenseCategory);
                categoryPrice.replace(expenseCategory, price);
            }
            else {
                categoryPrice.put(expenseCategory, expense.getPrice());
            }
        }
        return categoryPrice;
    }

    private static <K extends Comparable<K>, V extends Comparable<V>> Map<K, V> sort(
            final Map<K, V> unsorted,
            final boolean order) {
        final var list = new LinkedList<>(unsorted.entrySet());

        list.sort((o1, o2) -> order
                ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue())
                : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(
                Collectors.toMap(
                        Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new
                )
        );
    }


    public static void sendMonthCSV () {
        List<User> allUsers = userService.getUsers();
        for (User user : allUsers) {
            Set<Expense> userExpenses = getAllUserExpensesForPreviousMonth(user);
            long userTgId = user.getTg_id();
            File reportFile = new File("src/main/java/userscsv/report" + userTgId + ".csv");
            try {
                FileWriter outputFile = new FileWriter(reportFile);
                CSVWriter writer = new CSVWriter(outputFile);
                String[] header = {"id", "название", "категория", "сумма", "дата"};
                writer.writeNext(header);
                double expenseSum = 0.0;
                for (Expense expense : userExpenses) {
                    String id = Long.toString(expense.getId());
                    String name = expense.getName();
                    String cat = expense.getCategory().getName();
                    String price = Double.toString(expense.getPrice());
                    String date = expense.getDate().toString();

                    String[] data = {id, name, cat, price, date};
                    writer.writeNext(data);
                    expenseSum += expense.getPrice();
                }
                String[] data = {"всего:", " ", " ", Double.toString(expenseSum), " "};
                writer.writeNext(data);
                writer.close();
                TelegaBot.bot.execute(new SendDocument(userTgId, reportFile).caption("month report.csv"));
            }
            catch (IOException e) {}
            //id название категория сумма дата

        }
    }

    private static Set<Expense> getAllUserExpensesForPreviousMonth(User user) {
        Set<Expense> allExpenses = user.getExpenses();
        Set<Expense> monthExpenses = new HashSet<>();

        Calendar calendarThisMonth = Calendar.getInstance();
        calendarThisMonth.set(Calendar.DAY_OF_MONTH, 1);
        calendarThisMonth.set(Calendar.HOUR_OF_DAY, 0);
        Date currentMonth = calendarThisMonth.getTime();

        Calendar calendarPreviousMonth = Calendar.getInstance();
        calendarPreviousMonth.set(Calendar.DAY_OF_MONTH, 1);
        calendarPreviousMonth.set(Calendar.HOUR_OF_DAY, 0);
        calendarPreviousMonth.set(Calendar.MONTH, -1);
        Date previousMonth = calendarPreviousMonth.getTime();

        for (Expense expense : allExpenses) {
            if (expense.getDate().before(currentMonth) && expense.getDate().after(previousMonth)) {
                monthExpenses.add(expense);
            }
        }
        return monthExpenses;
    }
}
