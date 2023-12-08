package com.example.eccentricclothing.service;

import com.example.eccentricclothing.OrderNotFoundException;
import com.example.eccentricclothing.model.Order;
import com.example.eccentricclothing.model.OrderStatusUpdate;
import com.example.eccentricclothing.model.User;
import com.example.eccentricclothing.repository.OrderRepository;
import com.example.eccentricclothing.repository.OrderStatusUpdateRepository;
import com.example.eccentricclothing.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderStatusUpdateRepository orderStatusUpdateRepository;

    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));

        // Create OrderStatusUpdate
        OrderStatusUpdate statusUpdate = new OrderStatusUpdate();
        statusUpdate.setOrder(order);
        statusUpdate.setUpdatedStatus(newStatus);
        statusUpdate.setUpdateDateTime(LocalDateTime.now());
        switch (newStatus) {
            case PLACED:
                statusUpdate.setPlaceReached("Order Ready to ship");
                break;
            case SHIPPED:
                statusUpdate.setPlaceReached("Order left from warehouse");
                break;
            case OUT_FOR_DELIVERY:
                statusUpdate.setPlaceReached("Order has reached nearby courier facility");
                break;
            case DELIVERED:
                statusUpdate.setPlaceReached("Order delivered");
                break;
            default:

                break;
        }

        // Update Order status
        order.setStatus(newStatus);

        // Save changes
        orderRepository.save(order);
        orderStatusUpdateRepository.save(statusUpdate);
    }

    public void saveOrder(Order order) {
        orderRepository.save(order);
    }


    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    public void cancelOrder(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        Order order = optionalOrder.get();
        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

    }

    public List<Order> getAllOrder() {
        return orderRepository.findAll();
    }


    public void updateOrderStatus(Long orderId, String newStatus) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        Order order = optionalOrder.get();
        order.setStatus(OrderStatus.valueOf(newStatus));

        // Save the updated order back to the repository
        orderRepository.save(order);

    }

    public void returnOrder(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            if (OrderStatus.DELIVERED.equals(order.getStatus())) {
                // Only update the status to "Return" if the current status is "Delivered"
                order.setStatus(OrderStatus.RETURN);
                orderRepository.save(order);
            } else {

                throw new IllegalStateException("Order is not delivered and cannot be returned.");
            }
        } else {
            // Handle cases where the order with the given ID is not found

        }
    }

}
