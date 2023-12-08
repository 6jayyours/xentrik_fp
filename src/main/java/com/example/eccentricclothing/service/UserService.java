package com.example.eccentricclothing.service;

import com.example.eccentricclothing.model.Cart;
import com.example.eccentricclothing.model.Product;
import com.example.eccentricclothing.model.User;
import com.example.eccentricclothing.repository.CartRepository;
import com.example.eccentricclothing.repository.UserRepository;
import com.example.eccentricclothing.util.AuthenticationFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private AuthenticationFacade authenticationFacade;



    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id);
    }
    public void saveUser(User user) {
        userRepository.save(user);
    }

    public List<User> getUsersWithUserRole() {
        return userRepository.findUsersByRole("ROLE_USER");
    }


    public void blockUser(Integer userId, boolean enable) {
        User user = userRepository.findById(userId);
        if (user != null) {
            user.setEnabled(enable);
            user.setActive("Blocked");
            userRepository.save(user);
        }
    }

    public void unBlockUser(Integer userId, boolean enable) {
        User user = userRepository.findById(userId);
        if (user != null) {
            user.setEnabled(enable);
            user.setActive("Active");
            userRepository.save(user);
        }
    }


    @Transactional
    public void updateOtp(User user,String otp){
        user.setOtp(otp);
        userRepository.save(user);
    }



    @Transactional
    public void registerUser(User user) {
        // Generate OTP
        String otp = generateOTP();

        // Set the OTP (you may want to hash it before storing)
        user.setOtp(otp);
        user.setRole("ROLE_USER");


        // Save the user to the database (you may want to hash the password too)
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        userRepository.save(user);

        // Send OTP email
        sendOTPEmail(user.getEmail(), otp);
    }
    public String generateOTP() {
        // Generate a random 6-digit OTP
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append((int) (Math.random() * 10));
        }
        return otp.toString();
    }

    public void sendOTPEmail(String to, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("marjunramesh@gmail.com"); // Set your email address
        message.setTo(to);
        message.setSubject("OTP Verification");
        message.setText("Your OTP for registration is: " + otp);

        javaMailSender.send(message);
    }

    public boolean verifyOTP(String email, String otp) {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            String storedOtp = user.getOtp();
            if (storedOtp != null && storedOtp.trim().equals(otp.trim())) {
                user.setEnabled(true);
                user.setActive("active");
                userRepository.save(user);
                return true;
            }
        }
        return false;
    }


    public User getLoggedInUser() {
        String loggedInUserEmail = authenticationFacade.getAuthentication().getName();
        return userRepository.findUserByEmail(loggedInUserEmail).orElse(null);
    }

    public void updateUser(User updatedUser) {
        User existingUser = getLoggedInUser();

        if (existingUser != null) {
            // Update the user's details
            existingUser.setUsername(updatedUser.getUsername());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setGender(updatedUser.getGender());
            existingUser.setMobile(updatedUser.getMobile());

            // Save the updated user in the database
            userRepository.save(existingUser);
        }
    }

    public void addToUserCart(User user, Product product) {
        Cart cart = getOrCreateUserCart(user);
        cart.getProducts().add(product);
        cart.setTotalCartAmount(cart.getTotalPrice() + product.getPrice());

        cartRepository.save(cart);
    }


    private Cart getOrCreateUserCart(User user) {
        Cart cart = cartRepository.findByUser(user);
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cartRepository.save(cart);
        }
        return cart;
    }

    public Cart getUserCart(User user) {
        return getOrCreateUserCart(user);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public boolean passwordsMatch(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }

    public void resetPassword(String email, String password) {
        User user = findByEmail(email);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        userRepository.save(user);

    }


    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
