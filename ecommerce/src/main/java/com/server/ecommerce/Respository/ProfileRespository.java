package com.server.ecommerce.Respository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.Entity.User;
import com.server.ecommerce.Entity.Profile;

public interface ProfileRespository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUser(User user);

}
