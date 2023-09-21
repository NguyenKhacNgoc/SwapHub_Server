package com.server.ecommerce.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.server.ecommerce.DTO.ImageUploadResponse;
import com.server.ecommerce.DTO.PostResponseDTO;
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
public class PostController {
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

    @GetMapping("/getPost")
    public ResponseEntity<?> getPost(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            User user = userRespository.findByEmail(jwtTokenUtil.getEmailFromToken(token)).get();
            List<Posts> posts = postRespository.findByUser(user);
            List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
            for (Posts post : posts) {
                PostResponseDTO postResponseDTO = new PostResponseDTO();
                postResponseDTO.setId(post.getId());
                postResponseDTO.setCategory(post.getCategory());
                postResponseDTO.setDescription(post.getDescription());
                postResponseDTO.setEmail(post.getUser().getEmail());
                postResponseDTO.setPrice(post.getPrice());
                postResponseDTO.setTitle(post.getTitle());
                // Cái cloudinaryService này viết nhờ phương thức thôi, tại lười
                postResponseDTO.setImages(
                        cloudinaryService.copyImagesToImageUploadResponses(imageRespository.findByPost(post)));
                postResponseDTOs.add(postResponseDTO);
            }
            return ResponseEntity.ok(postResponseDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/testgetPost")
    public ResponseEntity<?> testgetPost(@RequestParam("id") Long id) {

        Optional<User> user = userRespository.findById(id);
        if (user.isPresent()) {
            List<Posts> posts = postRespository.findByUser(user.get());
            List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
            for (Posts post : posts) {
                PostResponseDTO postResponseDTO = new PostResponseDTO();
                postResponseDTO.setId(post.getId());
                postResponseDTO.setCategory(post.getCategory());
                postResponseDTO.setDescription(post.getDescription());
                postResponseDTO.setEmail(post.getUser().getEmail());
                postResponseDTO.setPrice(post.getPrice());
                postResponseDTO.setTitle(post.getTitle());

                // Cái cloudinaryService này viết nhờ phương thức thôi, tại lười
                postResponseDTO.setImages(
                        cloudinaryService.copyImagesToImageUploadResponses(imageRespository.findByPost(post)));
                postResponseDTOs.add(postResponseDTO);
            }
            return ResponseEntity.ok(postResponseDTOs);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    @DeleteMapping("/deletePost")
    public ResponseEntity<?> deletePost(@RequestHeader("Authorization") String authorization,
            @RequestParam("postID") Long postID, @RequestParam("userID") Long userID) throws IOException {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            User user = userRespository.findByEmail(email).get();
            Optional<User> existingUdel = userRespository.findById(userID);
            if (existingUdel.isPresent()) {
                if (existingUdel.get().getId() == user.getId()) {

                    Optional<Posts> existingPost = postRespository.findById(postID);
                    if (existingPost.isPresent()) {
                        List<Images> images = imageRespository.findByPost(existingPost.get());
                        for (Images img : images) {
                            String publicID = img.getPublicID();
                            // Gọi cloudinaryService để xoá ảnh khỏi kho lưu trữ
                            cloudinaryService.deleteImage(publicID);

                            // Xoá hình ảnh khỏi mysql
                            imageRespository.delete(img);

                        }
                        // Xoá bài viết khỏi cơ sở dữ liệu
                        postRespository.deleteById(postID);
                        return ResponseEntity.ok("Xoá thành công");
                    } else {
                        return ResponseEntity.badRequest().body("Bài viết này không tồn tại");
                    }

                } else {
                    return ResponseEntity.badRequest().body("Bạn không phải chủ sở hữu bài viết này");
                }
            } else {
                return ResponseEntity.badRequest().body("Người dùng không tồn tại");
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}
