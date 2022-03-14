package com.plytus.plytus.services;

import com.plytus.plytus.model.Expense;

import java.util.List;

public interface ExpenseService {

    List<Expense> getExpenses();

    Expense getExpenseById(Long id);

    Expense saveNewExpense(Expense expense);
}
