package com.server.ecommerce.Respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.Entity.User;

public interface UserRespository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
