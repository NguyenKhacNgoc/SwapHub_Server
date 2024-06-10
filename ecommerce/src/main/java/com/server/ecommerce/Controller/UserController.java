package com.server.ecommerce.Controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.server.ecommerce.DTO.AuthResponse;
import com.server.ecommerce.DTO.ChangePasswordRequest;
import com.server.ecommerce.DTO.ImageUploadResponse;

import com.server.ecommerce.DTO.UserDTO;
import com.server.ecommerce.Entity.Address;
import com.server.ecommerce.Entity.ImgReport;
import com.server.ecommerce.Entity.Posts;
import com.server.ecommerce.Entity.Report;
import com.server.ecommerce.Entity.User;
import com.server.ecommerce.JWT.JwtTokenUtil;
import com.server.ecommerce.Respository.ImgReportRespository;
import com.server.ecommerce.Respository.PostRespository;
import com.server.ecommerce.Respository.ReportRespository;

import com.server.ecommerce.Respository.UserRespository;
import com.server.ecommerce.Services.AuthServices;
import com.server.ecommerce.Services.CloudinaryService;
import com.server.ecommerce.Services.EmailServices;

@RestController
@RequestMapping("/api")
public class UserController {
    private final AuthServices authServices;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    // signup
    @Autowired
    private UserRespository userRespository;
    @Autowired
    private PostRespository postRespository;
    @Autowired
    private ReportRespository reportRespository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ImgReportRespository imgReportRespository;

    public UserController(AuthServices authServices, JwtTokenUtil jwtTokenUtil, UserRespository userRespository,
            EmailServices emailServices) {
        this.authServices = authServices;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRespository = userRespository;

    }

    // Login
    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody UserDTO request) {
        if (authServices.authenticate(request.getEmail(), request.getPassword())) {
            User user = userRespository.findByEmail(request.getEmail()).get();
            if (user.getStatus().equals("Bình thường")) {

                String token = authServices.generateToken(request.getEmail());
                AuthResponse authResponse = new AuthResponse(token);
                return ResponseEntity.ok(authResponse);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
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
    /*
     * // Định nghĩa cache với thời gian 5 phút
     * Cache<String, TempUser> verificationCodeCache =
     * CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
     * .build();
     * 
     * // SignUp
     * 
     * @PostMapping("/send-verification-code")
     * public ResponseEntity<?> sendVerificationCode(@RequestBody UserDTO request) {
     * Optional<User> existingUser =
     * userRespository.findByEmail(request.getEmail());
     * TempUser existingTempU =
     * verificationCodeCache.getIfPresent(request.getEmail());
     * 
     * if (existingUser.isPresent()) {
     * return ResponseEntity.badRequest().body("Tài khoản đã tồn tại");
     * } else {
     * if (existingTempU != null) {
     * Random random = new Random();
     * int randomCode = random.nextInt(999999 - 100000 + 1) + 100000;
     * 
     * // Gửi lại mã và thay bằng mã mới
     * emailServices.sendVerificationCode(request.getEmail(), randomCode);
     * existingTempU.setVerificationcode(randomCode);
     * verificationCodeCache.put(request.getEmail(), existingTempU); // Cập nhật lại
     * cache
     * return ResponseEntity.ok("Đã gửi lại mã");
     * } else {
     * Random random = new Random();
     * int randomCode = random.nextInt(999999 - 100000 + 1) + 100000;
     * // Gửi mã xác minh đến địa chỉ email của người dùng
     * emailServices.sendVerificationCode(request.getEmail(), randomCode);
     * TempUser tempUser = new TempUser();
     * tempUser.setEmail(request.getEmail());
     * tempUser.setVerificationcode(randomCode);
     * verificationCodeCache.put(request.getEmail(), tempUser); // Lưu vào cache
     * return ResponseEntity.ok("Gửi mã xác nhận thành công");
     * }
     * }
     * }
     * 
     * @GetMapping("/testgetcache")
     * public ResponseEntity<?> testgetcache(@RequestParam("email") String email) {
     * 
     * TempUser tempUser = verificationCodeCache.getIfPresent(email);
     * return ResponseEntity.ok(tempUser);
     * }
     * 
     * @PostMapping("/signup")
     * public ResponseEntity<?> SignUp(@RequestBody UserDTO request) {
     * Optional<User> existingUser =
     * userRespository.findByEmail(request.getEmail());
     * TempUser existingTempU =
     * verificationCodeCache.getIfPresent(request.getEmail());
     * if (existingUser.isPresent()) {
     * return ResponseEntity.badRequest().body("Tài khoản đã tồn tại");
     * } else {
     * if (existingTempU != null) {
     * if ((existingTempU.getEmail().equals(request.getEmail()))
     * && (existingTempU.getVerificationcode() == request.getVerificationCode())) {
     * User user = new User();
     * user.setEmail(request.getEmail());
     * user.setPassword(request.getPassword());
     * Address address = new Address();
     * address.setProvince(request.getProvince());
     * address.setDistrict(request.getDistrict());
     * address.setWard(request.getWard());
     * user.setAddress(address);
     * user.setDateofbirth(request.getDateofbirth());
     * user.setFullName(request.getFullName());
     * user.setPhoneNumber(request.getPhoneNumber());
     * user.setSex(request.getSex());
     * user.setRole("user");
     * user.setStatus("ok");
     * userRespository.save(user);
     * 
     * // Đăng ký xong xoá tempU
     * verificationCodeCache.invalidate(request.getEmail());
     * 
     * return ResponseEntity.ok("Đăng ký thành công");
     * 
     * } else {
     * return ResponseEntity.badRequest().body("Mã xác thực không chính xác");
     * }
     * } else {
     * return ResponseEntity.badRequest().body("Gửi lại mã xác nhận");
     * }
     * }
     * }
     */

    @GetMapping("/checkexistPNB")
    public ResponseEntity<?> checkexistPNB(@RequestParam("phoneNumber") String phoneNumber) {
        Optional<User> existingUser = userRespository.findByPhoneNumber(phoneNumber);
        if (existingUser.isPresent()) {
            return ResponseEntity.ok().body("Tài khoản này đã tồn tại");
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("/signup")
    public ResponseEntity<?> SignUpUser(@RequestBody UserDTO request) {
        Optional<User> existingUser = userRespository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body("Tài khoản đã tồn tại");
        } else {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(request.getPassword());
            Address address = new Address();
            address.setProvince(request.getProvince());
            address.setDistrict(request.getDistrict());
            address.setWard(request.getWard());
            user.setAddress(address);
            user.setDateofbirth(request.getDateofbirth());
            user.setFullName(request.getFullName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setSex(request.getSex());
            user.setRole("user");
            user.setStatus("Bình thường");
            userRespository.save(user);
            return ResponseEntity.ok("Đăng ký thành công");
        }

    }

    // Đổi mật khẩu
    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestHeader("Authorization") String authorization,
            @RequestBody ChangePasswordRequest request) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            User user = userRespository.findByEmail(email).get();
            if (request.getPassword().equals(user.getPassword())) {
                if (!request.getPassword().equals(request.getNewPassword())) {
                    user.setPassword(request.getNewPassword());
                    userRespository.save(user);
                    return ResponseEntity.ok("Đổi mật khẩu thành công");
                } else {
                    return ResponseEntity.badRequest().body("Mật khẩu mới không được trùng mật khẩu cũ");
                }

            } else {
                return ResponseEntity.badRequest().body("Mật khẩu hiện tại không đúng");
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }
    /*
     * - Chức năng quên mật khẩu - Lúc nào làm
     */

    // Tố cáo người dùng
    // Chưa test api được

    @PostMapping("/reportUser")
    public ResponseEntity<?> reportUser(@RequestHeader("Authorization") String authorization,
            @RequestParam("accused") Long accused, @RequestParam("reason") String reason,
            @RequestParam("postId") Long postID,
            @RequestParam("images") List<MultipartFile> images) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            Optional<User> existingAccused = userRespository.findById(accused);
            Optional<User> existingAccuser = userRespository.findByEmail(jwtTokenUtil.getEmailFromToken(token));
            if (existingAccused.isPresent()) {
                if (existingAccused.get().getId() == existingAccuser.get().getId()) {
                    return ResponseEntity.badRequest().body("Không thể tự tố cáo chính mình");

                } else {
                    Optional<Posts> existingPost = postRespository.findById(postID);
                    if (existingPost.isPresent()) {

                        try {
                            Report report = new Report();
                            report.setAccused(existingAccused.get());
                            report.setAccuser(existingAccuser.get());
                            report.setReason(reason);
                            report.setSendAt(LocalDateTime.now());
                            report.setStatus("Chờ xác minh");
                            report.setPost(existingPost.get());
                            reportRespository.save(report);
                            // Tải hình ảnh lên cloudinary
                            // List<MultipartFile> images = request.getImages();
                            for (MultipartFile image : images) {
                                ImageUploadResponse imageUploadResponse = cloudinaryService
                                        .uploadImage(image.getBytes());
                                String imgUrl = imageUploadResponse.getSecureUrl();
                                String publicId = imageUploadResponse.getPublicID();
                                ImgReport imgReport = new ImgReport();
                                imgReport.setImgUrl(imgUrl);
                                imgReport.setPublicID(publicId);
                                imgReport.setReport(report);
                                imgReportRespository.save(imgReport);

                            }
                            return ResponseEntity.ok("Tố cáo thành công");

                        } catch (Exception e) {
                            e.printStackTrace();
                            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                    .body("Lỗi trong quá trình tải hình ảnh");
                        }
                    } else {
                        return ResponseEntity.status(404).body("Bài viết bạn tố cáo không tồn tại");
                    }

                }

            } else {
                return ResponseEntity.badRequest().body("Người dùng mà bạn tố cáo không tồn tại");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Xác thực người dùng thất bại");
        }
    }

}
