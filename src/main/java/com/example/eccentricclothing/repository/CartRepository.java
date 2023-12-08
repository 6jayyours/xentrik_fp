package com.example.eccentricclothing.repository;

import com.example.eccentricclothing.model.Cart;
import com.example.eccentricclothing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {
    Cart findByUser(User user);

    List<Cart> findByUser_Id(Integer userId);



}
