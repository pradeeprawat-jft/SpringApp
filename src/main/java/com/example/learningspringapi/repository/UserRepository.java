package com.example.learningspringapi.repository;

import com.example.learningspringapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findAll();
    User findByEmail(String email);
    User findByEmailAndPassword(String email, String password);
    void deleteById(Integer id);
    Optional<User> findById(Integer id);
}
