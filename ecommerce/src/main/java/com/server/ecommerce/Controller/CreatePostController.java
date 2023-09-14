package com.server.ecommerce.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.server.ecommerce.DTO.ImageUploadResponse;
import com.server.ecommerce.Entity.Images;
import com.server.ecommerce.Entity.Posts;
import com.server.ecommerce.Entity.User;
import com.server.ecommerce.JWT.JwtTokenUtil;
import com.server.ecommerce.Respository.ImageRespository;
import com.server.ecommerce.Respository.PostRespository;
import com.server.ecommerce.Respository.UserRespository;
import com.server.ecommerce.Services.CloudinaryService;

@RestController
@RequestMapping("/api")
public class CreatePostController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserRespository userRespository;
    @Autowired
    private PostRespository postRespository;
    @Autowired
    private ImageRespository imageRespository;

    @PostMapping("/createpost")
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String authorization,
            @RequestParam("category") String category, @RequestParam("title") String title,
            @RequestParam("description") String description, @RequestParam("price") Float price,
            @RequestParam("images") List<MultipartFile> images) {

        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            User user = userRespository.findByEmail(email).get();
            try {
                // Lưu bài viết vào mysql trước đã
                Posts post = new Posts();
                post.setCategory(category);
                post.setDescription(description);
                post.setPrice(price);
                post.setTitle(title);
                post.setUser(user);
                postRespository.save(post);

                // Đẩy hình ảnh lên cloudinary và return về url
                for (MultipartFile image : images) {
                    ImageUploadResponse imageUploadResponse = cloudinaryService.uploadImage(image.getBytes());
                    String publicID = imageUploadResponse.getPublicID();
                    String imgUrl = imageUploadResponse.getSecureUrl();
                    Images img = new Images();
                    img.setPublicID(publicID);
                    img.setImgUrl(imgUrl);
                    img.setPost(post);
                    imageRespository.save(img);
                }
                return ResponseEntity.ok("Thành công");

            } catch (Exception e) {
                e.printStackTrace();

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Lỗi trong quá trình xử lý yêu cầu");
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

    }

}
