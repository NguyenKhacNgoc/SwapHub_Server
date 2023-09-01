package com.server.ecommerce.Services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.server.ecommerce.Entity.User;
import com.server.ecommerce.JWT.JwtTokenUtil;
import com.server.ecommerce.Respository.UserRespository;

@Service
public class AuthServices {

    private final UserRespository userRepository;

    private final JwtTokenUtil jwtTokenUtil;

    public AuthServices(UserRespository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public boolean authenticate(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            if (user.get().getPassword().equals(password)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public String generateToken(String email) {
        return jwtTokenUtil.generateToken(email);
    }

}
