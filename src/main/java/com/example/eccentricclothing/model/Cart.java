package com.example.eccentricclothing.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToMany
    private List<Product> products = new ArrayList<>();


    @ElementCollection
    @CollectionTable(name = "user_cart_selected_products", joinColumns = @JoinColumn(name = "cart_id"))
    @Column(name = "product_id")
    private List<Long> selectedProductIds = new ArrayList<>();

    private double totalPrice;
    

    public void setTotalCartAmount( double totalAmount){
        this.totalPrice = totalAmount;

    }

    public Cart(User user, List<Product> products) {
        this.user = user;
        this.products = products;
    }

    public Cart() {
    }

    public void setTotalCartAmount() {
        this.totalPrice = calculateTotalCartAmount();
    }

    private double calculateTotalCartAmount() {
        return products.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }

}
