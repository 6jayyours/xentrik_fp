package com.example.eccentricclothing.service;

import com.example.eccentricclothing.model.Product;
import com.example.eccentricclothing.model.ProductImage;
import com.example.eccentricclothing.repository.ProductImageRepository;
import com.example.eccentricclothing.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductImageRepository productImageRepository;
    public List<Product> getAllProduct() {
        return productRepository.findAll();
    }

    public void addProduct(Product product){
        productRepository.save(product);
    }

    public void removeProductById(long id){
        productRepository.deleteById(id);
    }

    public Optional<Product> getProductById(long id){
        return productRepository.findById(id);
    }
    public List<Product> getAllProductsByCategoryId(int id){
        return productRepository.findAllByCategory_Id(id);
    }

    public List<Product> getProductsByCategoryName(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }

    public Page<Product> findAllPaginated(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    public List<Product> getProductsByCategoryNameAndGender(String category, String gender) {
        return productRepository.findProductsByCategoryNameAndGender(category, gender);
    }

    public Product getProductById(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
            return productOptional.get();
    }

    public Long saveImageAndGetId(ProductImage image) {
        ProductImage savedImage = productImageRepository.save(image);
        return savedImage.getId();
    }

    public Long saveImageAndGetId(String imageName) {
        ProductImage image = new ProductImage();
        image.setImageName(imageName);
        ProductImage savedImage = productImageRepository.save(image);

        return savedImage.getId();
    }

    public List<Product> searchProducts(String query, String category) {
        return productRepository.findByCategoryName(query);
    }


    public List<Product> getProductsByFilters(List<String> brands, List<String> genders, List<String> sizes, double price) {
        if (brands == null && genders == null && sizes == null && price == 0) {
            // If no filters selected, return all products
            return productRepository.findAll();
        }

        if (brands != null && genders != null && sizes != null && price != 0) {
            // If all four filters are selected
            if (price <= 500) {
                return productRepository.findByBrandInAndGenderInAndSizeInAndPriceLessThanEqual(brands, genders, sizes, 500);
            } else if (price <= 1000) {
                return productRepository.findByBrandInAndGenderInAndSizeInAndPriceLessThanEqual(brands, genders, sizes, 1000);
            } else {
                return productRepository.findByBrandInAndGenderInAndSizeInAndPriceGreaterThan(brands, genders, sizes, 1000);
            }
        } else if (brands != null && genders != null && sizes != null) {
            // If brand names, genders, and sizes are selected
            return productRepository.findByBrandInAndGenderInAndSizeIn(brands, genders, sizes);
        } else if (brands != null && genders != null) {
            // If brand names and genders are selected
            return productRepository.findByBrandInAndGenderIn(brands, genders);
        } else if (brands != null && sizes != null) {
            // If brand names and sizes are selected
            return productRepository.findByBrandInAndSizeIn(brands, sizes);
        } else if (genders != null && sizes != null) {
            // If genders and sizes are selected
            return productRepository.findByGenderInAndSizeIn(genders, sizes);
        } else if (brands != null) {
            // If only brand names are selected
            return productRepository.findByBrandIn(brands);
        } else if (genders != null) {
            // If only genders are selected
            return productRepository.findByGenderIn(genders);
        } else if (sizes != null) {
            // If only sizes are selected
            return productRepository.findBySizeIn(sizes);
        } else if (price != 0) {
            // If only price is selected
            if (price <= 500) {
                return productRepository.findByPriceLessThanEqual(500);
            } else if (price <= 1000) {
                return productRepository.findByPriceLessThanEqual(1000);
            } else {
                return productRepository.findByPriceGreaterThan(1000);
            }
        }

        // Default case, return an empty list
        return Collections.emptyList();
    }

    @Transactional
    public void reduceProductQuantities(Map<Long, Integer> productCountMap) {
        for (Map.Entry<Long, Integer> entry : productCountMap.entrySet()) {
            Long productId = entry.getKey();
            Integer count = entry.getValue();

            // Retrieve the product from the database
            Product product = getProductById(productId);

            // Update the product quantity
            if (product != null) {
                int newQuantity = product.getQuantity() - count;
                product.setQuantity(newQuantity);
                // Assuming a method to save the product
                addProduct(product);
            }
        }
    }
}
