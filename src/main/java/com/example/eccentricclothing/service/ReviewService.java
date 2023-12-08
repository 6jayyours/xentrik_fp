package com.example.eccentricclothing.service;

import com.example.eccentricclothing.model.Product;
import com.example.eccentricclothing.model.Review;
import com.example.eccentricclothing.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Transactional
    public void saveReview(Review review) {
        reviewRepository.save(review);
    }


    public List<Review> getAllReviewsForProduct(Long productId) {
        return reviewRepository.findByProductId(productId);
    }

}
