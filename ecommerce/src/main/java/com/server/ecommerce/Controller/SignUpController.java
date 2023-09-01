package com.server.ecommerce.Controller;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.ecommerce.DTO.UserDTO;
import com.server.ecommerce.Entity.TempUser;
import com.server.ecommerce.Entity.User;
import com.server.ecommerce.Entity.Profile;
import com.server.ecommerce.Respository.TempUserRespository;
import com.server.ecommerce.Respository.ProfileRespository;
import com.server.ecommerce.Respository.UserRespository;
import com.server.ecommerce.Services.EmailServices;

@RestController
@RequestMapping("/api")
public class SignUpController {
    @Autowired
    private UserRespository userRespository;
    @Autowired
    private TempUserRespository tempUserRespository;
    @Autowired
    private EmailServices emailServices;
    @Autowired
    private ProfileRespository profileRespository;

    public SignUpController(UserRespository userRespository, TempUserRespository tempUserRespository,
            EmailServices emailServices, ProfileRespository profileRespository) {
        this.userRespository = userRespository;
        this.tempUserRespository = tempUserRespository;
        this.emailServices = emailServices;
        this.profileRespository = profileRespository;
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody UserDTO request) {
        Optional<User> existingUser = userRespository.findByEmail(request.getEmail());
        Optional<TempUser> existingTempU = tempUserRespository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Tài khoản đã tồn tại");
        } else {
            if (existingTempU.isPresent()) {
                Random random = new Random();
                int randomCode = random.nextInt(999999 - 100000 + 1) + 100000;

                // Gửi lại mã và thay bằng mã mới, set lại thời gian hết hạn
                emailServices.sendVerificationCode(request.getEmail(), randomCode);
                TempUser tempUser = existingTempU.get();
                tempUser.setVerificationcode(randomCode);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.MINUTE, 5);
                tempUser.setExpiredtime(calendar.getTime());
                tempUserRespository.save(tempUser);
                return ResponseEntity.ok("Đã gửi lại mã");
            } else {
                Random random = new Random();
                int randomCode = random.nextInt(999999 - 100000 + 1) + 100000;
                // Gửi mã xác minh đến địa chỉ email của người dùng
                emailServices.sendVerificationCode(request.getEmail(), randomCode);
                TempUser tempUser = new TempUser();
                tempUser.setEmail(request.getEmail());
                tempUser.setVerificationcode(randomCode);
                tempUser.setVerificated(false);
                // Mã xác nhận có thời hạn 5 phút
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.MINUTE, 5);
                tempUser.setExpiredtime(calendar.getTime());
                tempUserRespository.save(tempUser);
                return ResponseEntity.ok("Gửi mã xác nhận thành công");

            }

        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> SignUp(@RequestBody UserDTO request) {
        Optional<User> existingUser = userRespository.findByEmail(request.getEmail());
        Optional<TempUser> existingTempU = tempUserRespository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Tài khoản đã tồn tại");
        } else {
            if (existingTempU.isPresent()) {
                if ((existingTempU.get().getEmail().equals(request.getEmail()))
                        && (existingTempU.get().getVerificationcode() == request.getVerificationCode())) {
                    User user = new User();
                    user.setEmail(request.getEmail());
                    user.setPassword(request.getPassword());
                    userRespository.save(user);

                    // Đăng ký xong xoá tempU
                    tempUserRespository.delete(existingTempU.get());
                    // Tạo bảng thông tin người dùng
                    Profile profile = new Profile();
                    profile.setUser(user);
                    profileRespository.save(profile);

                    return ResponseEntity.ok("Đăng ký thành công");

                } else {
                    return ResponseEntity.badRequest().body("Mã xác thực không chính xác");
                }
            } else {
                return ResponseEntity.badRequest().body("Gửi lại mã xác nhận");
            }
        }
    }

}
