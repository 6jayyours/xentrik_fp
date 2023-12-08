package com.example.eccentricclothing.repository;

import com.example.eccentricclothing.model.Order;
import com.example.eccentricclothing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUser(User user);
}
