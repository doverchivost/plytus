package com.plytus.plytus.repository;

import com.plytus.plytus.model.Expense;
import org.springframework.data.repository.CrudRepository;

public interface ExpenseRepository extends CrudRepository<Expense, Long> {
    Expense findById(long id);
}
