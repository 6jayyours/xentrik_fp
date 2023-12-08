package com.example.eccentricclothing.repository;

import com.example.eccentricclothing.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    public Payment findByOrderId(String orderId);
}
