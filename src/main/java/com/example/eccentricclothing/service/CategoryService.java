package com.example.eccentricclothing.service;

import com.example.eccentricclothing.model.Category;
import com.example.eccentricclothing.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;


    public Category addCategory(Category category) {
         categoryRepository.save(category);
        return category;
    }
    public boolean isCategoryUnique(String categoryName) {
        Category existingCategory = categoryRepository.findByName(categoryName);
        return existingCategory == null;
    }


    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    public void removeById(int id){
        categoryRepository.deleteById(id);
    }


    public Category updateCat(Category category) {
        return categoryRepository.save(category);
    }

    public Optional<Category> getCategoryById (int id) {
        return categoryRepository.findById(id);
    }
}
