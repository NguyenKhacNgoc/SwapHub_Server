package com.server.ecommerce.Services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.server.ecommerce.DTO.Request.idRequest;
import com.server.ecommerce.DTO.Response.ImageUploadResponse;
import com.server.ecommerce.DTO.Response.PostResponseDTO;
import com.server.ecommerce.DTO.Response.UserDTOResponse;
import com.server.ecommerce.Entity.Category;
import com.server.ecommerce.Entity.ImgPost;
import com.server.ecommerce.Entity.Posts;
import com.server.ecommerce.Entity.User;
import com.server.ecommerce.Exception.AppException;
import com.server.ecommerce.Exception.ErrorCode;
import com.server.ecommerce.Mapper.UserMapper;
import com.server.ecommerce.Repository.CategoryRepository;
import com.server.ecommerce.Repository.PostImageRepository;
import com.server.ecommerce.Repository.PostRepository;

@Service
public class PostServices {
    @Autowired
    private PostImageRepository imageRespository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private UserService userService;

    public PostResponseDTO toPostResponse(Posts post) {

        List<UserDTOResponse> likedBys = new ArrayList<>();
        List<User> userlikeBys = post.getLikedBy();
        for (User user : userlikeBys) {
            likedBys.add(userMapper.toUserResponse(user));

        }
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .category(post.getCategory().getName())
                .price(post.getPrice())
                .status(post.getStatus())
                .description(post.getDescription())
                .postAt(post.getPostAt())
                .user(userMapper.toUserResponse(post.getUser()))
                .likedBy(likedBys)
                .images(copyImagesPostToImageUploadResponses(imageRespository.findByPost(post)))
                .build();

    }

    public List<ImageUploadResponse> copyImagesPostToImageUploadResponses(List<ImgPost> images) {
        List<ImageUploadResponse> listImageUploadResponses = new ArrayList<>();
        for (ImgPost img : images) {
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.setPublicID(img.getPublicID());
            imageUploadResponse.setSecureUrl(img.getImgUrl());
            listImageUploadResponses.add(imageUploadResponse);
        }
        return listImageUploadResponses;
    }

    public List<PostResponseDTO> postResponseDTOToList(List<Posts> posts) {
        List<PostResponseDTO> postResponseDTOs = new ArrayList<>();
        for (Posts post : posts) {
            postResponseDTOs.add(toPostResponse(post));
        }
        return postResponseDTOs;
    }

    public List<Category> getAllCategory() {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXIST);
        }
        return categories;
    }

    public PostResponseDTO createNewPost(String category, String title, String description, Float price,
            List<MultipartFile> images) throws IOException {
        Posts post = Posts.builder()
                .category(categoryRepository.findById(category)
                        .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST)))
                .description(description)
                .price(price)
                .title(title)
                .postAt(LocalDateTime.now())
                .user(userService.getUser())
                .status("pending")
                .build();
        cloudinaryService.imagetoCloudinary(images, postRepository.save(post));
        PostResponseDTO postResponseDTO = toPostResponse(post);
        return postResponseDTO;

    }

    public List<PostResponseDTO> getListMyPost() {
        List<Posts> posts = postRepository.findByUser(userService.getUser());
        if (posts.isEmpty()) {
            throw new AppException(ErrorCode.POST_NOT_EXIST);
        }

        return postResponseDTOToList(posts);
    }

    public List<PostResponseDTO> getAllPost() {
        List<Posts> posts = postRepository.findByStatusOk();
        return postResponseDTOToList(posts);
    }

    public List<PostResponseDTO> getPendingPost() {
        List<Posts> posts = postRepository.findByStatusPending();
        if (posts.isEmpty()) {
            throw new AppException(ErrorCode.POST_NOT_EXIST);
        }
        return postResponseDTOToList(posts);
    }

    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    public List<PostResponseDTO> getPostsByUser(String email) {
        List<Posts> posts = postRepository.findByUser(userService.getUserFromEmail(email));
        if (posts.isEmpty()) {
            throw new AppException(ErrorCode.POST_NOT_EXIST);
        }
        return postResponseDTOToList(posts);

    }

    public PostResponseDTO getPostByID(String postID) {
        Posts post = postRepository.findById(postID).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXIST));
        return toPostResponse(post);
    }

    public List<PostResponseDTO> getPostsOfUser(String email) {
        List<Posts> posts = postRepository.findByUser(userService.getUserFromEmail(email));
        return postResponseDTOToList(posts);
    }

    @PostAuthorize("returnObject.user.email == authentication.name")
    public PostResponseDTO hidePost(idRequest request) {
        Posts post = postRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXIST));
        post.setStatus("hide");
        return toPostResponse(postRepository.save(post));
    }

    public PostResponseDTO acpPost(idRequest request) {
        Posts post = postRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXIST));
        post.setStatus("ok");
        return toPostResponse(postRepository.save(post));
    }

    public Boolean checklikedPost(String postID) {

        User user = userService.getUser();
        Posts post = postRepository.findById(postID)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXIST));
        List<User> likedBy = post.getLikedBy();
        if (likedBy.contains(user)) {
            return true;

        } else {
            return false;
        }
    }

    public PostResponseDTO likePost(idRequest request) {
        Posts post = postRepository.findById(request.getId())
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXIST));
        List<User> likedBy = post.getLikedBy();
        User user = userService.getUser();
        // Kiểm tra xem người dùng đã thích bài viết này hay chưa
        if (!likedBy.contains(user))
            likedBy.add(user);
        // Nếu đã like thì unlike
        else
            likedBy.remove(user);
        post.setLikedBy(likedBy);
        return toPostResponse(postRepository.save(post));

    }

    public List<PostResponseDTO> getPostIsLiked() {
        List<Posts> allpost = postRepository.findAll();
        List<Posts> likedPosts = allpost.stream()
                .filter(post -> post.getLikedBy().stream()
                        .anyMatch(user -> user.getId().equals(userService.getUser().getId())))
                .collect(Collectors.toList());

        return postResponseDTOToList(likedPosts);
    }

    public List<PostResponseDTO> searchTittle(String text) {
        List<Posts> posts = postRepository.findByTitle(text);
        if (posts.isEmpty()) {
            throw new AppException(ErrorCode.POST_NOT_EXIST);
        }
        return postResponseDTOToList(posts);
    }

    public List<PostResponseDTO> findByCategory(String categoryID) {
        Category category = categoryRepository.findById(categoryID)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_EXIST));
        List<Posts> posts = postRepository.findByCategory(category);
        if (posts.isEmpty()) {
            throw new AppException(ErrorCode.POST_NOT_EXIST);
        }
        return postResponseDTOToList(posts);
    }

    public List<PostResponseDTO> sortByPrice(Float minPrice, Float maxPrice) {
        List<Posts> posts = postRepository.findByPriceRange(minPrice, maxPrice);
        if (posts.isEmpty()) {
            throw new AppException(ErrorCode.POST_NOT_EXIST);
        }
        return postResponseDTOToList(posts);
    }

    public List<UserDTOResponse> getUserOfLikePost(String postID) {
        Posts post = postRepository.findById(postID).orElseThrow(() -> new AppException(ErrorCode.POST_NOT_EXIST));
        List<User> users = post.getLikedBy();
        List<UserDTOResponse> userDTOResponses = new ArrayList<>();
        for (User user : users) {
            userDTOResponses.add(userMapper.toUserResponse(user));
        }
        return userDTOResponses;
    }
}
