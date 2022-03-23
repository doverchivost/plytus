package com.plytus.plytus.repository;

import com.plytus.plytus.model.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {
    Category findById(long id);
}
