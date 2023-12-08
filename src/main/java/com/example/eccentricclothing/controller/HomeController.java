package com.example.eccentricclothing.controller;

import com.example.eccentricclothing.model.User;
import com.example.eccentricclothing.repository.UserRepository;
import com.example.eccentricclothing.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {



    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/")
    public String landingPage(){
        return "landing-page";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }



    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, HttpSession session) {
        userService.registerUser(user);
        session.setAttribute("registrationEmail", user.getEmail());
        return "otpverification";
    }


    @PostMapping("/verify")
    public String verifyOTP(@RequestParam String otp, @RequestParam String email,HttpSession session) {
        String registrationEmail = (String) session.getAttribute("registrationEmail");
        boolean isVerified = userService.verifyOTP(registrationEmail, otp);
        if (isVerified) {
            return "login";
        } else {
            return "otpverification";
        }
    }

    @GetMapping("/logout")
    public String logout() {
        SecurityContextHolder.clearContext(); // Clear security context
        return "redirect:/login";
    }

    @GetMapping("/forgotpassword")
    public String forgotPassword(){
        return "sendotpmail";
    }

    @PostMapping("/enterotp")
    public String enterotp(@RequestParam("email") String email,HttpSession session){
        String otp = userService.generateOTP();
        userService.sendOTPEmail(email,otp);
        User user = userService.findByEmail(email);
        userService.updateOtp(user,otp);
        session.setAttribute("registrationEmail", email);
        return "sendotp";
    }

    @PostMapping("/passwordenter")
    public String passwordResetPage(@RequestParam String otp, @RequestParam String email,HttpSession session){
        String registrationEmail = (String) session.getAttribute("registrationEmail");
        boolean isVerified = userService.verifyOTP(registrationEmail, otp);
        session.setAttribute("registrationEmail", registrationEmail);
        if (isVerified) {
            return "password";
        } else {
            return "sendotp";
        }
    }

    @PostMapping("/passwordreset")
    public String resetPassword(
            @RequestParam("password") String password,
            @RequestParam("passwordConfirmation") String passwordConfirmation,
            @RequestParam("email") String email,
            HttpSession session,
            Model model
    ) {
        String registrationEmail = (String) session.getAttribute("registrationEmail");
        System.out.println(registrationEmail);
        boolean match = userService.passwordsMatch(password, passwordConfirmation);
        if (match) {
            userService.resetPassword(registrationEmail, password);
            return "login";
        } else {
            model.addAttribute("error", "Passwords do not match. Please try again.");
            return "password";
        }
    }





}
