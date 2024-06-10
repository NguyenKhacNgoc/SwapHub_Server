package com.server.ecommerce.Controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.server.ecommerce.Entity.Category;
import com.server.ecommerce.Entity.ImgPost;
import com.server.ecommerce.Entity.Posts;
import com.server.ecommerce.Entity.User;
import com.server.ecommerce.JWT.JwtTokenUtil;
import com.server.ecommerce.Respository.CategoryRespository;
import com.server.ecommerce.Respository.ImageRespository;
import com.server.ecommerce.Respository.PostRespository;

import com.server.ecommerce.Respository.UserRespository;
import com.server.ecommerce.Services.CloudinaryService;
import com.server.ecommerce.Services.ResponseServices;
import com.server.ecommerce.Services.SSEServices;

@RestController
@RequestMapping("/api")
public class PostController {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private ResponseServices responseServices;
    @Autowired
    private UserRespository userRespository;
    @Autowired
    private PostRespository postRespository;
    @Autowired
    private ImageRespository imageRespository;
    @Autowired
    private SSEServices sseServices;
    @Autowired
    private CategoryRespository categoryRespository;

    @GetMapping("/getCategory")
    public ResponseEntity<?> getCategory(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            List<Category> categories = categoryRespository.findAll();
            return ResponseEntity.ok(categories);

        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/getNameCategory")
    public ResponseEntity<?> getNameCategory() {

        List<Category> categories = categoryRespository.findAll();
        List<String> categoriesName = new ArrayList<>();
        categoriesName.add("Tất cả");
        for (Category category : categories) {
            String Strcategory = category.getName();
            categoriesName.add(Strcategory);
        }

        return ResponseEntity.ok(categoriesName);

    }

    @PostMapping("/createpost")
    public ResponseEntity<?> createPost(@RequestHeader("Authorization") String authorization,
            @RequestParam("category") Long category, @RequestParam("title") String title,
            @RequestParam("description") String description, @RequestParam("price") Float price,
            @RequestParam("images") List<MultipartFile> images) {

        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            User user = userRespository.findByEmail(email).get();
            try {
                // Lưu bài viết vào mysql
                Posts post = new Posts();
                post.setCategory(categoryRespository.findById(category).get());
                post.setDescription(description);
                post.setPrice(price);
                post.setTitle(title);
                post.setPostAt(LocalDateTime.now());
                post.setUser(user);
                post.setStatus("pending");
                postRespository.save(post);

                // Đẩy hình ảnh lên cloudinary và trả về url
                for (MultipartFile image : images) {
                    ImageUploadResponse imageUploadResponse = cloudinaryService.uploadImage(image.getBytes());
                    String publicID = imageUploadResponse.getPublicID();
                    String imgUrl = imageUploadResponse.getSecureUrl();
                    ImgPost img = new ImgPost();
                    img.setPublicID(publicID);
                    img.setImgUrl(imgUrl);
                    img.setPost(post);
                    imageRespository.save(img);
                }
                sseServices.updatePosts("create 200");
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
    public ResponseEntity<?> getallPost() {
        List<Posts> posts = postRespository.findByStatusOk();
        List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
        for (Posts post : posts) {
            postResponseDTOs.add(responseServices.postResponse(post));
        }
        return ResponseEntity.ok(postResponseDTOs);

    }

    @GetMapping("/getmyPost")
    public ResponseEntity<?> getPost(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            User user = userRespository.findByEmail(jwtTokenUtil.getEmailFromToken(token)).get();
            List<Posts> posts = postRespository.findByUser(user);
            List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
            for (Posts post : posts) {
                postResponseDTOs.add(responseServices.postResponse(post));
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

    @PutMapping("/hidePost")
    public ResponseEntity<?> hidePost(@RequestHeader("Authorization") String authorization,
            @RequestBody PostUpdateDTO request) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            User user = userRespository.findByEmail(email).get();
            Optional<Posts> expost = postRespository.findById(request.getId());
            if (expost.isPresent()) {
                if (user.getId() == expost.get().getUser().getId()) {
                    Posts post = expost.get();
                    post.setStatus("hide");
                    postRespository.save(post);
                    return ResponseEntity.ok().body("Ẩn bài viết thành công");
                } else
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Không thể ẩn bài viết của người khác");
            } else
                return ResponseEntity.status(404).body("Bài viết không tòn tại");
        } else
            return ResponseEntity.status(401).body("Xác thực thất bại");

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
                // Nếu user đăng nhập là admin thì cho xoá tất
                if (user.getRole().equals("admin")) {
                    List<ImgPost> images = imageRespository.findByPost(existingpost.get());
                    for (ImgPost img : images) {
                        String publicID = img.getPublicID();
                        // Gọi cloudinary để xoá ảnh khỏi kho lưu trữ
                        cloudinaryService.deleteImage(publicID);
                        // Xoá bảng hình ảnh khỏi cơ sở dữ liệu
                        imageRespository.delete(img);
                    }
                    // Xoá bài viết khỏi cơ sở dữ liệu
                    postRespository.deleteById(requestdelete.getId());
                    sseServices.updatePosts("deleted 200");
                    return ResponseEntity.ok("Bài viết đã được xoá");

                } else {
                    if (user.getId() == existingpost.get().getUser().getId()) {
                        List<ImgPost> images = imageRespository.findByPost(existingpost.get());
                        for (ImgPost img : images) {
                            String publicID = img.getPublicID();
                            // Gọi cloudinary để xoá ảnh khỏi kho lưu trữ
                            cloudinaryService.deleteImage(publicID);
                            // Xoá bảng hình ảnh khỏi cơ sở dữ liệu
                            imageRespository.delete(img);
                        }
                        // Xoá bài viết khỏi cơ sở dữ liệu
                        postRespository.deleteById(requestdelete.getId());
                        sseServices.updatePosts("deleted 200");
                        return ResponseEntity.ok("Bài viết đã được xoá");

                    } else {
                        return ResponseEntity.badRequest().body("Không thể xoá bài viết của người khác");
                    }

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

    @GetMapping("/checklikedPost")
    public ResponseEntity<?> checklikedPost(@RequestHeader("Authorization") String authorization,
            @RequestParam("postID") Long postID) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            User user = userRespository.findByEmail(jwtTokenUtil.getEmailFromToken(token)).get();
            Optional<Posts> existingPost = postRespository.findById(postID);
            if (existingPost.isPresent()) {
                Posts post = existingPost.get();
                List<User> likedBy = post.getLikedBy();
                if (likedBy.contains(user)) {
                    return ResponseEntity.ok("liked");

                } else {
                    return ResponseEntity.ok("like");
                }

            } else {
                return ResponseEntity.badRequest().build();
            }

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

    // Phần này để thích bài viết

    @PostMapping("/post/like")
    public ResponseEntity<?> likepost(@RequestHeader("Authorization") String authorization,
            @RequestBody PostUpdateDTO request) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            User user = userRespository.findByEmail(jwtTokenUtil.getEmailFromToken(token)).get();
            Optional<Posts> existingPost = postRespository.findById(request.getId());
            if (existingPost.isPresent()) {
                Posts post = existingPost.get();
                List<User> likedBy = post.getLikedBy();
                // Kiểm tra xem người dùng đã thích bài viết này hay chưa
                if (!likedBy.contains(user))
                    likedBy.add(user);
                // Nếu đã like thì unlike
                else
                    likedBy.remove(user);
                post.setLikedBy(likedBy);
                postRespository.save(post);
                return ResponseEntity.ok().build();

            } else
                return ResponseEntity.badRequest().body("Bài viết này không tồn tại");
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/getPostisLiked")
    public ResponseEntity<?> getPostisLiked(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            Long userID = userRespository.findByEmail(jwtTokenUtil.getEmailFromToken(token)).get().getId();

            List<Posts> allpost = postRespository.findAll();
            List<Posts> likedPosts = allpost.stream()
                    .filter(post -> post.getLikedBy().stream().anyMatch(user -> user.getId().equals(userID)))
                    .collect(Collectors.toList());

            List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
            for (Posts post : likedPosts) {
                postResponseDTOs.add(responseServices.postResponse(post));
            }
            return ResponseEntity.ok(postResponseDTOs);

        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/search/result")
    public ResponseEntity<?> searchText(@RequestParam("searchText") String searchText) {
        List<Posts> searchResult = postRespository.findByTitle(searchText);
        List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
        for (Posts post : searchResult) {
            postResponseDTOs.add(responseServices.postResponse(post));
        }
        return ResponseEntity.ok(postResponseDTOs);
    }

    // Lọc theo thể loại

    @GetMapping("/sort/sortByCategory")
    public ResponseEntity<?> sortByCategory(@RequestParam("categoryID") Long categoryID) {
        if (categoryRespository.findById(categoryID).isPresent()) {
            Category category = categoryRespository.findById(categoryID).get();
            List<Posts> posts = postRespository.findByCategory(category);
            List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
            for (Posts post : posts) {
                postResponseDTOs.add(responseServices.postResponse(post));

            }
            return ResponseEntity.ok(postResponseDTOs);
        } else {
            return ResponseEntity.badRequest().build();
        }

    }

    // Lọc theo giá
    @GetMapping("/sort/sortByPrice")
    public ResponseEntity<?> sortByPrice(@RequestParam("minPrice") Float minPrice,
            @RequestParam("maxPrice") Float maxPrice) {
        List<Posts> posts = postRespository.findByPriceRange(minPrice, maxPrice);
        List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
        for (Posts post : posts) {
            postResponseDTOs.add(responseServices.postResponse(post));

        }
        return ResponseEntity.ok(postResponseDTOs);

    }

    // Lấy những người thích 1 bài viết
    @GetMapping("/view/getNumberOfLike")
    public ResponseEntity<?> getNumberOfLike(@RequestParam("postID") Long postID) {
        Posts post = postRespository.findById(postID).get();
        List<User> users = post.getLikedBy();
        List<ProfileDTO> profileDTOs = new ArrayList<>();
        for (User user : users) {
            profileDTOs.add(responseServices.responseProfileDTO(user));

        }
        return ResponseEntity.ok(profileDTOs);

    }

}
