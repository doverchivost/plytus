package com.plytus.plytus.services;

import com.plytus.plytus.model.Category;
import com.plytus.plytus.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceDefault implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceDefault(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getCategories() {
        List<Category> result = new ArrayList<>();
        categoryRepository.findAll().forEach(result::add);
        return result;
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).get();
    }

    @Override
    public Category saveNewCategory(Category category) {
        return categoryRepository.save(category);
    }
}
