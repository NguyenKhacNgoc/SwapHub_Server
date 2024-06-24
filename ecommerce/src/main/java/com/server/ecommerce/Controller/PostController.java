package com.server.ecommerce.Controller;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.server.ecommerce.DTO.Request.idRequest;
import com.server.ecommerce.DTO.Response.ApiResponse;
import com.server.ecommerce.DTO.Response.PostResponseDTO;
import com.server.ecommerce.Services.PostServices;

@RestController
@RequestMapping("/api")
public class PostController {
    @Autowired
    private PostServices postServices;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/getCategory")
    public ApiResponse<?> getCategory() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.getAllCategory());
        return apiResponse;
    }

    // @GetMapping("/getNameCategory")
    // public ResponseEntity<?> getNameCategory() {

    // List<Category> categories = categoryRespository.findAll();
    // List<String> categoriesName = new ArrayList<>();
    // categoriesName.add("Tất cả");
    // for (Category category : categories) {
    // String Strcategory = category.getName();
    // categoriesName.add(Strcategory);
    // }

    // return ResponseEntity.ok(categoriesName);

    // }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostMapping("/createpost")
    public ApiResponse<PostResponseDTO> createPost(@RequestHeader("Authorization") String authorization,
            @RequestParam("category") String category, @RequestParam("title") String title,
            @RequestParam("description") String description, @RequestParam("price") Float price,
            @RequestParam("images") List<MultipartFile> images) throws IOException {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.createNewPost(category, title, description, price, images));
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/getallPost")
    public ApiResponse<?> getallPost() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.getAllPost());
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/getmyPost")
    public ApiResponse<?> getListMyPost() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.getListMyPost());
        return apiResponse;
    }

    // @PutMapping("/putPost")
    // public ResponseEntity<?> putPost(@RequestHeader("Authorization") String
    // authorization,
    // @RequestBody PostUpdateDTO requestPut) {
    // String token = authorization.substring(7);
    // if (jwtTokenUtil.validateToken(token)) {
    // String email = jwtTokenUtil.getEmailFromToken(token);
    // User user = userRespository.findByEmail(email).get();
    // Optional<Posts> existingpost = postRespository.findById(requestPut.getId());
    // if (existingpost.isPresent()) {
    // if (user.getId() == existingpost.get().getUser().getId()) {
    // // Sửa ở đây nhé
    // Posts post = existingpost.get();
    // post.setDescription(requestPut.getDescription());
    // post.setPrice(requestPut.getPrice());
    // post.setTitle(requestPut.getTitle());
    // postRespository.save(post);

    // return ResponseEntity.ok().body("Sửa bài viết thành công");

    // } else {
    // return ResponseEntity.badRequest().body("Không thể sửa bài viết của người
    // khác");
    // }

    // } else {
    // return ResponseEntity.badRequest().body("Bài viết này không tồn tại");
    // }

    // } else {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Xác thực thất
    // bại");
    // }

    // }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PutMapping("/hidePost")
    public ApiResponse<?> hidePost(@RequestBody idRequest request) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.hidePost(request));
        return apiResponse;

    }

    // @DeleteMapping("/deletePost")
    // public ResponseEntity<?> deletePost(@RequestHeader("Authorization") String
    // authorization,
    // @RequestBody PostUpdateDTO requestdelete) throws IOException {
    // String token = authorization.substring(7);
    // if (jwtTokenUtil.validateToken(token)) {
    // String email = jwtTokenUtil.getEmailFromToken(token);
    // User user = userRespository.findByEmail(email).get();

    // Optional<Posts> existingpost =
    // postRespository.findById(requestdelete.getId());
    // if (existingpost.isPresent()) {
    // // Nếu user đăng nhập là admin thì cho xoá tất
    // if (user.getRole().equals("admin")) {
    // List<ImgPost> images = imageRespository.findByPost(existingpost.get());
    // for (ImgPost img : images) {
    // String publicID = img.getPublicID();
    // // Gọi cloudinary để xoá ảnh khỏi kho lưu trữ
    // cloudinaryService.deleteImage(publicID);
    // // Xoá bảng hình ảnh khỏi cơ sở dữ liệu
    // imageRespository.delete(img);
    // }
    // // Xoá bài viết khỏi cơ sở dữ liệu
    // postRespository.deleteById(requestdelete.getId());

    // return ResponseEntity.ok("Bài viết đã được xoá");

    // } else {
    // if (user.getId() == existingpost.get().getUser().getId()) {
    // List<ImgPost> images = imageRespository.findByPost(existingpost.get());
    // for (ImgPost img : images) {
    // String publicID = img.getPublicID();
    // // Gọi cloudinary để xoá ảnh khỏi kho lưu trữ
    // cloudinaryService.deleteImage(publicID);
    // // Xoá bảng hình ảnh khỏi cơ sở dữ liệu
    // imageRespository.delete(img);
    // }
    // // Xoá bài viết khỏi cơ sở dữ liệu
    // postRespository.deleteById(requestdelete.getId());

    // return ResponseEntity.ok("Bài viết đã được xoá");

    // } else {
    // return ResponseEntity.badRequest().body("Không thể xoá bài viết của người
    // khác");
    // }

    // }

    // } else {
    // return ResponseEntity.badRequest().body("Bài viết này không tồn tại");
    // }

    // } else {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Xác thực thất
    // bại");
    // }
    // }

    // @PostMapping("/checkpost")
    // public ResponseEntity<?> checkpost(@RequestHeader("Authorization") String
    // authorization,
    // @RequestBody PostUpdateDTO request) {
    // String token = authorization.substring(7);
    // if (jwtTokenUtil.validateToken(token)) {
    // String email = jwtTokenUtil.getEmailFromToken(token);
    // User user = userRespository.findByEmail(email).get();
    // Optional<Posts> existingPost = postRespository.findById(request.getId());
    // if (existingPost.isPresent()) {
    // if (user.getId() == existingPost.get().getUser().getId())
    // return ResponseEntity.ok("OK");
    // else
    // return ResponseEntity.ok("Not OK");
    // } else
    // return ResponseEntity.badRequest().build();
    // } else
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    // }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/checklikedPost")
    public ApiResponse<?> checklikedPost(@RequestParam("postID") String postID) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.checklikedPost(postID));
        return apiResponse;
    }

    // Phần này để thích bài viết

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostMapping("/post/like")
    public ApiResponse<?> likepost(@RequestBody idRequest request) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.likePost(request));
        return apiResponse;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/getPostisLiked")
    public ApiResponse<?> getPostisLiked() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.getPostIsLiked());
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/search/result")
    public ApiResponse<?> searchText(@RequestParam("searchText") String searchText) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.searchTittle(searchText));
        return apiResponse;
    }

    // Lọc theo thể loại
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @GetMapping("/sort/sortByCategory")
    public ApiResponse<?> sortByCategory(@RequestParam("categoryID") String categoryID) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.findByCategory(categoryID));
        return apiResponse;
    }

    // Lọc theo giá
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/sort/sortByPrice")
    public ApiResponse<?> sortByPrice(@RequestParam("minPrice") Float minPrice,
            @RequestParam("maxPrice") Float maxPrice) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.sortByPrice(minPrice, maxPrice));
        return apiResponse;

    }

    // Lấy những người thích 1 bài viết
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/view/getNumberOfLike")
    public ApiResponse<?> getNumberOfLike(@RequestParam("postID") String postID) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.getUserOfLikePost(postID));
        return apiResponse;
    }

}
