package com.server.ecommerce.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.ecommerce.Entity.User;
import com.server.ecommerce.Entity.Profile;
import com.server.ecommerce.JWT.JwtTokenUtil;
import com.server.ecommerce.Respository.ProfileRespository;
import com.server.ecommerce.Respository.UserRespository;

@RestController
@RequestMapping("/api")
public class GetProfileController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRespository userRespository;
    @Autowired
    private ProfileRespository profileRespository;

    @GetMapping("/getprofile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            User user = userRespository.findByEmail(email).get();
            Optional<Profile> existingUser = profileRespository.findByUser(user);
            if (existingUser.isPresent()) {
                return ResponseEntity.ok(existingUser.get());
            } else {
                return ResponseEntity.badRequest().body("Không tìm thấy thông tin người dùng");
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
