package com.server.ecommerce.Respository;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.server.ecommerce.Entity.TempUser;

public interface TempUserRespository extends JpaRepository<TempUser, Long> {
    Optional<TempUser> findByEmail(String email);

    @Transactional
    void deleteByVerificatedFalseAndExpiredtimeLessThanEqual(Date currentTime);
}
