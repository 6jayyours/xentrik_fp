package com.example.eccentricclothing.model;

import lombok.Data;

import com.example.eccentricclothing.util.OrderStatus;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Data
@Table(name = "order_status_updates")
public class OrderStatusUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "updateDateTime")
    private LocalDateTime updateDateTime;

    @Column(name = "place_reached")
    private String placeReached;

    @Enumerated(EnumType.STRING)
    @Column(name = "updated_status")
    private OrderStatus updatedStatus;



}

