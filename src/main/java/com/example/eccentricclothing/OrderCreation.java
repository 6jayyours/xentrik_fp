//package com.example.eccentricclothing;
//
//import javax.servlet.ServletException;
//import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//import org.json.*;
//import com.razorpay.*;
//
//public class OrderCreation extends HttpServlet {
//    public OrderCreation(){}
//
//    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        RazorpayClient client = null;
//        String orderId = null;
//
//        try {
//            client =new RazorpayClient("rzp_test_SFjHv4xXWflKId","04OdyFbM1EuvizwDFUaD9p8D");
//            JSONObject options = new JSONObject();
//            options.put("amount","100");
//            options.put("currency","INR");
//            options.put("reciept","zxr456");
//            options.put("payment_capture",true);
//            Order order = client.orders.create(options);
//            orderId = order.get("id");
//        } catch (RazorpayException e) {
//            e.printStackTrace();
//        }
//        response.getWriter().append(orderId);
//
//    }
//
//    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException {
//        RazorpayClient client = null;
//        try{
//            client = new RazorpayClient("rzp_test_SFjHv4xXWflKId","04OdyFbM1EuvizwDFUaD9p8D");
//            JSONObject options =new JSONObject();
//            options.put("razorpay_payment_id",request.getParameter("razorpay_payment_id"));
//            options.put("razorpay_order_id",request.getParameter("razorpay_order_id"));
//            options.put("razorpay_signature",request.getParameter("razorpay_signature"));
//            boolean sigRes = Utils.verifyPaymentLink(options,"04OdyFbM1EuvizwDFUaD9p8D");
//            if(sigRes) {
//                response.getWriter().append("Payment Successfull");
//            } else {
//                response.getWriter().append("Payment failed");
//            }
//        } catch (RazorpayException e) {
//            e.printStackTrace();
//        }
//    }
//}
