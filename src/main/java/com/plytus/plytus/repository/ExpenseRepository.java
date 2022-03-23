package com.plytus.plytus.repository;

import com.plytus.plytus.model.Expense;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ExpenseRepository extends CrudRepository<Expense, Long> {
    Expense findById(long id);
    void delete(Expense expense);

    @Modifying
    @Query("delete from Expense e where e.id=:id")
    void queryDeleteById(@Param("id") Long id);
}
