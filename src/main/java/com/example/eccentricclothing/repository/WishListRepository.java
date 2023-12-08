package com.example.eccentricclothing.repository;

import com.example.eccentricclothing.model.WishList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishListRepository extends JpaRepository<WishList,Long> {

    List<WishList> findByUserId(Integer userId);

    boolean existsByUser_IdAndProduct_Id(Integer userId, Long productId);


    void deleteProductByUserIdAndProductId(Integer userId, Long productId);
}
