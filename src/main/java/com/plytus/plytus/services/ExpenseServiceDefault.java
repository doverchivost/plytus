package com.plytus.plytus.services;

import com.plytus.plytus.model.Expense;
import com.plytus.plytus.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class ExpenseServiceDefault implements ExpenseService {

    private final ExpenseRepository expenseRepository;

    @Autowired
    public  ExpenseServiceDefault(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @Override
    public List<Expense> getExpenses() {
        List<Expense> result = new ArrayList<>();
        expenseRepository.findAll().forEach(result::add);
        return result;
    }

    @Override
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).get();
    }

    @Override
    public Expense saveNewExpense(Expense expense) {
        return expenseRepository.save(expense);
    }

    @Override
    @Transactional
    public void deleteExpense(Expense expense) {
        expenseRepository.queryDeleteById(expense.getId());
    }
}
