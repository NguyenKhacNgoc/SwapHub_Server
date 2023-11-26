package com.server.ecommerce.Controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.server.ecommerce.Entity.User;
import com.server.ecommerce.DTO.ProfileDTO;

import com.server.ecommerce.JWT.JwtTokenUtil;

import com.server.ecommerce.Respository.UserRespository;

@RestController
@RequestMapping("/api")
public class ProfileController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRespository userRespository;

    @GetMapping("/checkauth")
    public ResponseEntity<?> checkauth(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token))
            return ResponseEntity.status(HttpStatus.OK).build();
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/getprofile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            Optional<User> existingUser = userRespository.findByEmail(email);
            if (existingUser.isPresent()) {
                ProfileDTO profileDTO = new ProfileDTO();
                profileDTO.setAddress(existingUser.get().getAddress());
                profileDTO.setDateofbirth(existingUser.get().getDateofbirth());
                profileDTO.setFullName(existingUser.get().getFullName());
                profileDTO.setEmail(existingUser.get().getEmail());
                profileDTO.setPhoneNumber(existingUser.get().getPhoneNumber());
                profileDTO.setSex(existingUser.get().getSex());
                return ResponseEntity.ok(profileDTO);
            } else {
                return ResponseEntity.badRequest().body("Không tìm thấy thông tin người dùng");
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    // @GetMapping("/testgetprofile")
    // public ResponseEntity<?> testgetProfile(@RequestParam("email") String email)
    // {
    // Optional<User> exsitingU = userRespository.findByEmail(email);

    // if (exsitingU.isPresent()) {

    // User user = exsitingU.get();
    // Optional<Profile> existingUser = profileRespository.findByUser(user);
    // if (existingUser.isPresent()) {
    // ProfileDTO profileDTO = new ProfileDTO();
    // profileDTO.setAddress(existingUser.get().getAddress());
    // profileDTO.setDateofbirth(existingUser.get().getDateofbirth());
    // profileDTO.setFullName(existingUser.get().getFullName());
    // profileDTO.setEmail(existingUser.get().getUser().getEmail());
    // profileDTO.setPhoneNumber(existingUser.get().getPhoneNumber());
    // profileDTO.setSex(existingUser.get().getSex());
    // return ResponseEntity.ok(profileDTO);
    // } else {
    // return ResponseEntity.badRequest().body("Không tìm thấy thông tin người
    // dùng");
    // }

    // } else {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }
    // }

    @PutMapping("/putprofile")
    public ResponseEntity<?> putProfile(@RequestHeader("Authorization") String authorization,
            @RequestBody ProfileDTO request) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            User user = userRespository.findByEmail(email).get();

            user.setFullName(request.getFullName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setAddress(request.getAddress());
            user.setSex(request.getSex());
            user.setDateofbirth(request.getDateofbirth());
            userRespository.save(user);

            return ResponseEntity.ok("Thành công");

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
