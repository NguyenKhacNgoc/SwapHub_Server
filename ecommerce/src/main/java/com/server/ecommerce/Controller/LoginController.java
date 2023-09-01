package com.server.ecommerce.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.ecommerce.DTO.AuthResponse;
import com.server.ecommerce.DTO.UserDTO;
import com.server.ecommerce.Services.AuthServices;

@RestController
@RequestMapping("/api")
public class LoginController {
    private final AuthServices authServices;

    public LoginController(AuthServices authServices) {
        this.authServices = authServices;
    }

    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody UserDTO request) {
        if (authServices.authenticate(request.getEmail(), request.getPassword())) {
            String token = authServices.generateToken(request.getEmail());
            AuthResponse authResponse = new AuthResponse(token);
            return ResponseEntity.ok(authResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

}
