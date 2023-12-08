package com.example.eccentricclothing.repository;

import com.example.eccentricclothing.model.Address;
import com.example.eccentricclothing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address,Long> {
    List<Address> findByUser(User user);
}
