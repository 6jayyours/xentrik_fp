package com.example.eccentricclothing.dto;

import com.example.eccentricclothing.model.Category;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private int categoryId;
    private double price;
    private String description;
    private String gender;
    private String brand;
    private int quantity;
    private List<String> imageNames;
    private List<MultipartFile> images;

}
