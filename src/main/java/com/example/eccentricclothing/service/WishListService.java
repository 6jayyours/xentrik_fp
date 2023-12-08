package com.example.eccentricclothing.service;

import com.example.eccentricclothing.model.Product;
import com.example.eccentricclothing.model.User;
import com.example.eccentricclothing.model.WishList;
import com.example.eccentricclothing.repository.ProductRepository;
import com.example.eccentricclothing.repository.UserRepository;
import com.example.eccentricclothing.repository.WishListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class WishListService {

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    public void addToWishlist(Integer userId, Long productId) {
        User user = userRepository.findById(userId);
        Product product = productRepository.findById(productId).orElse(null);

        if (user != null && product != null) {
            WishList wishlistEntry = new WishList();
            wishlistEntry.setUser(user);
            wishlistEntry.setProduct(product);
            wishListRepository.save(wishlistEntry);
        }
    }

    public List<WishList> getProductsByUserId(Integer userId) {

         List<WishList> wishlists = wishListRepository.findByUserId(userId);
         return wishlists;
    }

    public boolean isProductInWishlist(Integer userId, Long productId) {
        return wishListRepository.existsByUser_IdAndProduct_Id(userId, productId);
    }


    @Transactional
    public void removeWishListById(Integer userId, Long productId) {
        wishListRepository.deleteProductByUserIdAndProductId(userId, productId);
    }
}
