package com.example.eccentricclothing.model;

import com.example.eccentricclothing.util.OrderStatus;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "order_date_time")
    private LocalDateTime orderDateTime;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "shipping_address_string")
    private String shippingAddressString;


    private String productName;


}
