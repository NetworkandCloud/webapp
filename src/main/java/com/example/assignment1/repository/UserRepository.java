package com.example.assignment1.repository;

import com.example.assignment1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User,UUID> {

    User findByUsername(String username);
}
