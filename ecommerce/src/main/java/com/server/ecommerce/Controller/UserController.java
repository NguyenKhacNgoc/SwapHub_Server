package com.server.ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.ecommerce.DTO.Request.ChangePasswordRequest;
import com.server.ecommerce.DTO.Request.UserDTO;
import com.server.ecommerce.DTO.Response.ApiResponse;

import com.server.ecommerce.DTO.Response.UserDTOResponse;

import com.server.ecommerce.Services.UserService;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @PostMapping("/signup")
    public ApiResponse<UserDTOResponse> createUser(@RequestBody UserDTO request) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.createUser(request));
        return apiResponse;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @GetMapping("getAllUser")
    public ApiResponse<?> getAllUser() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getAllUser());
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("getIn4OfUserByEmail")
    public ApiResponse<?> getIn4OfUserByEmail(@RequestParam("email") String email) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getIn4OfUser(email));
        return apiResponse;

    }

    // Đổi mật khẩu
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PutMapping("/changePassword")
    public ApiResponse<UserDTOResponse> changePassWord(@RequestBody ChangePasswordRequest request) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.changePassWord(request));
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/getprofile")
    public ApiResponse<UserDTOResponse> getMyInfo() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.getMyInfo());
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PutMapping("/putprofile")
    public ApiResponse<UserDTOResponse> updateUser(@RequestBody UserDTO request) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(userService.updateUser(request));
        return apiResponse;
    }
    /*
     * - Chức năng quên mật khẩu - Lúc nào làm
     */

    // Tố cáo người dùng
    /*
     * @PostMapping("/reportUser")
     * public ResponseEntity<?> reportUser(@RequestHeader("Authorization") String
     * authorization,
     * 
     * @RequestParam("accused") Long accused, @RequestParam("reason") String reason,
     * 
     * @RequestParam("postId") Long postID,
     * 
     * @RequestParam("images") List<MultipartFile> images) {
     * String token = authorization.substring(7);
     * if (jwtTokenUtil.validateToken(token)) {
     * Optional<User> existingAccused = userRespository.findById(accused);
     * Optional<User> existingAccuser =
     * userRespository.findByEmail(jwtTokenUtil.getEmailFromToken(token));
     * if (existingAccused.isPresent()) {
     * if (existingAccused.get().getId() == existingAccuser.get().getId()) {
     * return ResponseEntity.badRequest().body("Không thể tự tố cáo chính mình");
     * 
     * } else {
     * Optional<Posts> existingPost = postRespository.findById(postID);
     * if (existingPost.isPresent()) {
     * 
     * try {
     * Report report = new Report();
     * report.setAccused(existingAccused.get());
     * report.setAccuser(existingAccuser.get());
     * report.setReason(reason);
     * report.setSendAt(LocalDateTime.now());
     * report.setStatus("Chờ xác minh");
     * report.setPost(existingPost.get());
     * reportRespository.save(report);
     * // Tải hình ảnh lên cloudinary
     * // List<MultipartFile> images = request.getImages();
     * for (MultipartFile image : images) {
     * ImageUploadResponse imageUploadResponse = cloudinaryService
     * .uploadImage(image.getBytes());
     * String imgUrl = imageUploadResponse.getSecureUrl();
     * String publicId = imageUploadResponse.getPublicID();
     * ImgReport imgReport = new ImgReport();
     * imgReport.setImgUrl(imgUrl);
     * imgReport.setPublicID(publicId);
     * imgReport.setReport(report);
     * imgReportRespository.save(imgReport);
     * 
     * }
     * return ResponseEntity.ok("Tố cáo thành công");
     * 
     * } catch (Exception e) {
     * e.printStackTrace();
     * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
     * .body("Lỗi trong quá trình tải hình ảnh");
     * }
     * } else {
     * return ResponseEntity.status(404).body("Bài viết bạn tố cáo không tồn tại");
     * }
     * 
     * }
     * 
     * } else {
     * return
     * ResponseEntity.badRequest().body("Người dùng mà bạn tố cáo không tồn tại");
     * }
     * } else {
     * return ResponseEntity.status(HttpStatus.UNAUTHORIZED).
     * body("Xác thực người dùng thất bại");
     * }
     * }
     */

}
