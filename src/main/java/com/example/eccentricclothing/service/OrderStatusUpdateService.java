package com.example.eccentricclothing.service;

import com.example.eccentricclothing.model.OrderStatusUpdate;
import com.example.eccentricclothing.repository.OrderStatusUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderStatusUpdateService {

    @Autowired
    private OrderStatusUpdateRepository orderStatusUpdateRepository;

    public List<OrderStatusUpdate> getOrderStatusUpdatesByOrderId(Long orderId) {
        return orderStatusUpdateRepository.findByOrderId(orderId);
    }

    public void saveOrderStatusUpdate(OrderStatusUpdate orderStatusUpdate) {
        orderStatusUpdateRepository.save(orderStatusUpdate);
    }
}
