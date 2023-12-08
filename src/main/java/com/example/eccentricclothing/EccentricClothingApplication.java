package com.example.eccentricclothing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

import javax.servlet.annotation.MultipartConfig;

@MultipartConfig
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class EccentricClothingApplication {

	public static void main(String[] args) {
		SpringApplication.run(EccentricClothingApplication.class, args);
	}

}
