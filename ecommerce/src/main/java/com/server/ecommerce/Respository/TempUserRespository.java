package com.server.ecommerce.Respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.Entity.TempUser;

public interface TempUserRespository extends JpaRepository<TempUser, Long> {
    Optional<TempUser> findByEmail(String email);

}
