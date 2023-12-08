package com.example.eccentricclothing.repository;

import com.example.eccentricclothing.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {
    Category findById(Long categoryId);

    Category findByName(String categoryName);
}
