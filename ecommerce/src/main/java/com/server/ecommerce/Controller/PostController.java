package com.server.ecommerce.controller;

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

import com.server.ecommerce.dto.request.idRequest;
import com.server.ecommerce.dto.response.ApiResponse;
import com.server.ecommerce.dto.response.PostResponseDTO;
import com.server.ecommerce.services.CategoryService;
import com.server.ecommerce.services.PostServices;

@RestController
@RequestMapping("/api")
public class PostController {
    @Autowired
    private PostServices postServices;
    @Autowired
    private CategoryService categoryService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/category")
    public ApiResponse<?> getCategory() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(categoryService.getAllCategory());
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PostMapping("/post/create")
    public ApiResponse<PostResponseDTO> createPost(@RequestHeader("Authorization") String authorization,
            @RequestParam("category") String category, @RequestParam("title") String title,
            @RequestParam("description") String description, @RequestParam("price") Float price,
            @RequestParam("images") List<MultipartFile> images) throws IOException {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.createNewPost(category, title, description, price, images));
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/posts")
    public ApiResponse<?> getallPost() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.getAllPost());
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("/post")
    public ApiResponse<?> getListMyPost() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.getListMyPost());
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @PutMapping("/post/hide")
    public ApiResponse<?> hidePost(@RequestBody idRequest request) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.hidePost(request));
        return apiResponse;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("post/checklikedPost")
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
    @GetMapping("post/getPostisLiked")
    public ApiResponse<?> getPostisLiked() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.getPostIsLiked());
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("post/search/result")
    public ApiResponse<?> searchText(@RequestParam("searchText") String searchText) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.searchTittle(searchText));
        return apiResponse;
    }

    // Lọc theo thể loại
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @GetMapping("post/sort/category")
    public ApiResponse<?> sortByCategory(@RequestParam("categoryID") String categoryID) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.findByCategory(categoryID));
        return apiResponse;
    }

    // Lọc theo giá
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("post/sort/price")
    public ApiResponse<?> sortByPrice(@RequestParam("minPrice") Float minPrice,
            @RequestParam("maxPrice") Float maxPrice) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.sortByPrice(minPrice, maxPrice));
        return apiResponse;

    }

    // Lấy những người thích 1 bài viết
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("post/view/getNumberOfLike")
    public ApiResponse<?> getNumberOfLike(@RequestParam("postID") String postID) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(postServices.getUserOfLikePost(postID));
        return apiResponse;
    }

}
