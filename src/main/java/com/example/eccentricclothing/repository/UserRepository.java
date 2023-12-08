package com.example.eccentricclothing.repository;

import com.example.eccentricclothing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);

    User findById(Integer id);
    List<User> findUsersByRole(String role);

    Optional<User> findUserByEmail(String email);

    User findByUsername(String username);
}
