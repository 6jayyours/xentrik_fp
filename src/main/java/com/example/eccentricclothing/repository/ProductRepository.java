package com.example.eccentricclothing.repository;

import com.example.eccentricclothing.model.Category;
import com.example.eccentricclothing.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    List<Product> findAllByCategory_Id(int id);

    Optional<Product> findByName(String name);

    List<Product> findByCategoryName(String categoryName);

    List<Product> findProductsByCategoryNameAndGender(String category, String gender);


    List<Product> findByBrandInAndGenderIn(List<String> brands, List<String> genders);

    List<Product> findByBrandIn(List<String> brands);

    List<Product> findByGenderIn(List<String> genders);

    List<Product> findByBrandInAndGenderInAndSizeIn(List<String> brands, List<String> genders, List<String> sizes);

    List<Product> findByBrandInAndSizeIn(List<String> brands, List<String> sizes);

    List<Product> findByGenderInAndSizeIn(List<String> genders, List<String> sizes);

    List<Product> findBySizeIn(List<String> sizes);

    List<Product> findByBrandInAndGenderInAndSizeInAndPriceLessThanEqual(List<String> brands, List<String> genders, List<String> sizes, double price);

    List<Product> findByPriceLessThanEqual(double price);

    List<Product> findByBrandInAndGenderInAndSizeInAndPriceGreaterThan(List<String> brands, List<String> genders, List<String> sizes, int i);

    List<Product> findByPriceGreaterThan(int i);
}
