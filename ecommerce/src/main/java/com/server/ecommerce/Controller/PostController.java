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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.server.ecommerce.DTO.ImageUploadResponse;

import com.server.ecommerce.DTO.PostResponseDTO;
import com.server.ecommerce.DTO.PostUpdateDTO;
import com.server.ecommerce.DTO.ProfileDTO;
import com.server.ecommerce.Entity.Images;
import com.server.ecommerce.Entity.Posts;
import com.server.ecommerce.Entity.Profile;
import com.server.ecommerce.Entity.User;
import com.server.ecommerce.JWT.JwtTokenUtil;
import com.server.ecommerce.Respository.ImageRespository;
import com.server.ecommerce.Respository.PostRespository;
import com.server.ecommerce.Respository.ProfileRespository;
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
    @Autowired
    private ProfileRespository profileRespository;

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

    @GetMapping("/getallPost")
    public ResponseEntity<?> getallPost(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            List<Posts> posts = postRespository.findAll();
            List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
            for (Posts post : posts) {
                PostResponseDTO postResponseDTO = new PostResponseDTO();
                postResponseDTO.setId(post.getId());
                postResponseDTO.setCategory(post.getCategory());
                postResponseDTO.setDescription(post.getDescription());
                postResponseDTO.setPrice(post.getPrice());
                postResponseDTO.setTitle(post.getTitle());

                Profile profile = profileRespository.findByUser(post.getUser()).get();
                ProfileDTO profileDTO = new ProfileDTO();
                profileDTO.setAddress(profile.getAddress());
                profileDTO.setDateofbirth(profile.getDateofbirth());
                profileDTO.setFullName(profile.getFullName());
                profileDTO.setEmail(profile.getUser().getEmail());
                profileDTO.setPhoneNumber(profile.getPhoneNumber());
                profileDTO.setSex(profile.getSex());
                postResponseDTO.setProfile(profileDTO);

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

    @GetMapping("/getmyPost")
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
                postResponseDTO.setPrice(post.getPrice());
                postResponseDTO.setTitle(post.getTitle());

                Profile profile = profileRespository.findByUser(user).get();
                ProfileDTO profileDTO = new ProfileDTO();
                profileDTO.setAddress(profile.getAddress());
                profileDTO.setDateofbirth(profile.getDateofbirth());
                profileDTO.setFullName(profile.getFullName());
                profileDTO.setEmail(profile.getUser().getEmail());
                profileDTO.setPhoneNumber(profile.getPhoneNumber());
                profileDTO.setSex(profile.getSex());
                postResponseDTO.setProfile(profileDTO);

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

    @PutMapping("/putPost")
    public ResponseEntity<?> putPost(@RequestHeader("Authorization") String authorization,
            @RequestBody PostUpdateDTO requestPut) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            User user = userRespository.findByEmail(email).get();
            Optional<Posts> existingpost = postRespository.findById(requestPut.getId());
            if (existingpost.isPresent()) {
                if (user.getId() == existingpost.get().getUser().getId()) {
                    // Sửa ở đây nhé
                    Posts post = existingpost.get();
                    post.setDescription(requestPut.getDescription());
                    post.setPrice(requestPut.getPrice());
                    post.setTitle(requestPut.getTitle());
                    postRespository.save(post);

                    return ResponseEntity.ok().body("Sửa bài viết thành công");

                } else {
                    return ResponseEntity.badRequest().body("Không thể sửa bài viết của người khác");
                }

            } else {
                return ResponseEntity.badRequest().body("Bài viết này không tồn tại");
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Xác thực thất bại");
        }

    }

    @DeleteMapping("/deletePost")
    public ResponseEntity<?> deletePost(@RequestHeader("Authorization") String authorization,
            @RequestBody PostUpdateDTO requestdelete) throws IOException {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            User user = userRespository.findByEmail(email).get();
            Optional<Posts> existingpost = postRespository.findById(requestdelete.getId());
            if (existingpost.isPresent()) {
                if (user.getId() == existingpost.get().getUser().getId()) {
                    List<Images> images = imageRespository.findByPost(existingpost.get());
                    for (Images img : images) {
                        String publicID = img.getPublicID();
                        // Gọi cloudinary để xoá ảnh khỏi kho lưu trữ
                        cloudinaryService.deleteImage(publicID);
                        // Xoá bảng hình ảnh khỏi cơ sở dữ liệu
                        imageRespository.delete(img);
                    }
                    // Xoá bài viết khỏi cơ sở dữ liệu
                    postRespository.deleteById(requestdelete.getId());
                    return ResponseEntity.ok("Bài viết đã được xoá");

                } else {
                    return ResponseEntity.badRequest().body("Không thể xoá bài viết của người khác");
                }

            } else {
                return ResponseEntity.badRequest().body("Bài viết này không tồn tại");
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Xác thực thất bại");
        }
    }

    @PostMapping("/checkpost")
    public ResponseEntity<?> checkpost(@RequestHeader("Authorization") String authorization,
            @RequestBody PostUpdateDTO request) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            User user = userRespository.findByEmail(email).get();
            Optional<Posts> existingPost = postRespository.findById(request.getId());
            if (existingPost.isPresent()) {
                if (user.getId() == existingPost.get().getUser().getId())
                    return ResponseEntity.ok("OK");
                else
                    return ResponseEntity.ok("Not OK");
            } else
                return ResponseEntity.badRequest().build();
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

}
