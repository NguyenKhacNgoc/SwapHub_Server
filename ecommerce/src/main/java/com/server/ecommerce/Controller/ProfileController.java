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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.ecommerce.Entity.User;
import com.server.ecommerce.DTO.ProfileDTO;
import com.server.ecommerce.Entity.Profile;
import com.server.ecommerce.JWT.JwtTokenUtil;
import com.server.ecommerce.Respository.ProfileRespository;
import com.server.ecommerce.Respository.UserRespository;

@RestController
@RequestMapping("/api")
public class ProfileController {
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
                ProfileDTO profileDTO = new ProfileDTO();
                profileDTO.setAddress(existingUser.get().getAddress());
                profileDTO.setDateofbirth(existingUser.get().getDateofbirth());
                profileDTO.setFullName(existingUser.get().getFullName());
                profileDTO.setEmail(existingUser.get().getUser().getEmail());
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
    @GetMapping("/checkUpdateProfile")
    public ResponseEntity<?> checkUpdateProfile(@RequestHeader("Authorization") String authorization){
        String token = authorization.substring(7);
        if(jwtTokenUtil.validateToken(token)){
            String email = jwtTokenUtil.getEmailFromToken(token);
            User user = userRespository.findByEmail(email).get();
            Profile profile = profileRespository.findByUser(user).get();
            if(profile.getAddress() != null && profile.getDateofbirth() !=null && profile.getFullName() !=null && profile.getPhoneNumber() != null && profile.getSex() !=null){
                return ResponseEntity.ok("OK");
            }
            else return ResponseEntity.ok("Not OK");
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/testgetprofile")
    public ResponseEntity<?> testgetProfile(@RequestParam("email") String email) {
        Optional<User> exsitingU = userRespository.findByEmail(email);

        if (exsitingU.isPresent()) {

            User user = exsitingU.get();
            Optional<Profile> existingUser = profileRespository.findByUser(user);
            if (existingUser.isPresent()) {
                ProfileDTO profileDTO = new ProfileDTO();
                profileDTO.setAddress(existingUser.get().getAddress());
                profileDTO.setDateofbirth(existingUser.get().getDateofbirth());
                profileDTO.setFullName(existingUser.get().getFullName());
                profileDTO.setEmail(existingUser.get().getUser().getEmail());
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

    @PutMapping("/putprofile")
    public ResponseEntity<?> putProfile(@RequestHeader("Authorization") String authorization,
            @RequestBody ProfileDTO request) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            User user = userRespository.findByEmail(email).get();
            Optional<Profile> existingUser = profileRespository.findByUser(user);
            if (existingUser.isPresent()) {
                Profile profile = existingUser.get();
                profile.setFullName(request.getFullName());
                profile.setPhoneNumber(request.getPhoneNumber());
                profile.setAddress(request.getAddress());
                profile.setSex(request.getSex());
                profile.setDateofbirth(request.getDateofbirth());
                profileRespository.save(profile);

                return ResponseEntity.ok("Thành công");
            } else {
                return ResponseEntity.badRequest().body("Người dùng không tồn tại, vui lòng thử lại sau");
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
