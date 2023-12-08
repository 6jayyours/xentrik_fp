package com.example.eccentricclothing.model;

import lombok.Data;
import javax.persistence.*;


@Entity
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    private String name;
    private String mobile;


    private String pincode;

    private String housename;

    private String street;

    private String landmark;

    private String city;

    private String state;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Address(Address address) {
        this.name = address.getName();
        this.pincode = address.getPincode();
        this.housename = address.getHousename();
        this.street = address.getStreet();
        this.landmark = address.getLandmark();
        this.city = address.getCity();
        this.state = address.getState();
    }

    public Address(){

    }

    public String getFullAddress() {

        return getHousename() + ", " + getStreet() + ", " + getCity() + ", " +
                getState() + ", " + getLandmark() + ", " + getPincode() + ", " +
                "Mobile: " + getMobile();

    }
}
