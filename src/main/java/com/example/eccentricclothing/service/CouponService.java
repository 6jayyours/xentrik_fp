package com.example.eccentricclothing.service;

import com.example.eccentricclothing.model.Coupon;
import com.example.eccentricclothing.model.Product;
import com.example.eccentricclothing.repository.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CouponService {
    @Autowired
    private CouponRepository couponRepository;

    public void saveCoupon(Coupon coupon) {
        couponRepository.save(coupon);
    }

    public List<Coupon> getAllCoupon() {
        return couponRepository.findAll();
    }

    public void deleteCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    public Optional<Coupon> getCouponById(Long id) {
        return couponRepository.findById(id);
    }
}
