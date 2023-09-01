package com.server.ecommerce.Config;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.server.ecommerce.Respository.TempUserRespository;

@Component
public class UserVerificationCleanup {
    @Autowired
    private TempUserRespository tempUserRepository;

    @Scheduled(fixedDelay = 60000)
    public void cleanupUnverifiedTempUsers() {
        Date currentTime = new Date();
        tempUserRepository.deleteByVerificatedFalseAndExpiredtimeLessThanEqual(currentTime);

    }

}
