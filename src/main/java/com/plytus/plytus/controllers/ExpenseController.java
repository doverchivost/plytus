/*
package com.plytus.plytus.controllers;

import com.plytus.plytus.model.Expense;
import com.plytus.plytus.repository.ExpenseRepository;
import com.plytus.plytus.services.ExpenseService;
import org.apache.tomcat.util.security.Escape;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public String showAllExpenses(Model model) {
        model.addAllAttributes("expenses", expenseService.findAll());
        return "main/expenses/all";
    }

}

*/
