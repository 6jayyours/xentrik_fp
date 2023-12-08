package com.example.eccentricclothing.service;

import com.example.eccentricclothing.model.Cart;
import com.example.eccentricclothing.model.Product;
import com.example.eccentricclothing.model.User;
import com.example.eccentricclothing.repository.CartRepository;
import com.example.eccentricclothing.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductRepository productRepository;


    public List<Cart> getCartItemsForCurrentUser(Integer userId) {
        return cartRepository.findByUser_Id(userId);
    }

    @Transactional
    public void removeProductFromCart(Long productId) {
        User user = userService.getLoggedInUser();
        Cart userCart = cartRepository.findByUser(user);

        // Remove all products with the given productId
        userCart.getProducts().removeIf(product -> product.getId().equals(productId));

        userCart.setTotalCartAmount();
        cartRepository.save(userCart);
    }




}
