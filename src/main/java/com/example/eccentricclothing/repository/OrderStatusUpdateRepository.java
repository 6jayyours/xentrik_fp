package com.example.eccentricclothing.repository;

import com.example.eccentricclothing.model.OrderStatusUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusUpdateRepository extends JpaRepository<OrderStatusUpdate,Long> {

//    List<OrderStatusUpdate> findByOrderId(Long orderId);

    @Query("SELECT osu FROM OrderStatusUpdate osu JOIN FETCH osu.order WHERE osu.order.id = :orderId")
    List<OrderStatusUpdate> findByOrderId(@Param("orderId") Long orderId);

}
