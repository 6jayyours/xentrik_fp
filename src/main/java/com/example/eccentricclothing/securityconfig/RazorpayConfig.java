//package com.example.eccentricclothing.securityconfig;
//
//import com.razorpay.RazorpayClient;
//import com.razorpay.RazorpayException;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RazorpayConfig {
//
//    @Value("${razorpay.apiKey}")
//    private String apiKey;
//
//    @Value("${razorpay.apiSecret}")
//    private String apiSecret;
//
//    public RazorpayClient razorpayClient() {
//        try {
//            return new RazorpayClient(apiKey, apiSecret);
//        } catch (RazorpayException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to create RazorpayClient", e);
//        }
//    }
//
//}
