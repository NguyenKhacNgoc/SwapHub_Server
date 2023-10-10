package com.server.ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.ecommerce.DTO.AuthResponse;
import com.server.ecommerce.DTO.UserDTO;
import com.server.ecommerce.JWT.JwtTokenUtil;
import com.server.ecommerce.Services.AuthServices;

@RestController
@RequestMapping("/api")
public class LoginController {
    private final AuthServices authServices;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

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

    @GetMapping("/auth/login")
    public ResponseEntity<?> authlogin(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
