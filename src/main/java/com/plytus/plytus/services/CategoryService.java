package com.plytus.plytus.services;

import com.plytus.plytus.model.Category;

import java.util.List;

public interface CategoryService {

    List<Category> getCategories();

    Category getCategoryById(Long id);

    Category saveNewCategory(Category category);
}
