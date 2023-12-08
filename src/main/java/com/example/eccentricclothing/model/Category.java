package com.example.eccentricclothing.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "category_id")
    private int id;

    private String name;

    private String description;
}
