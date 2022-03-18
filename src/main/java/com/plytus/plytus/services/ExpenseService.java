package com.plytus.plytus.services;

import com.plytus.plytus.model.Expense;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ExpenseService {

    List<Expense> getExpenses();

    Expense getExpenseById(Long id);

    Expense saveNewExpense(Expense expense);

    @Transactional
    void deleteExpense(Expense expense);
}
