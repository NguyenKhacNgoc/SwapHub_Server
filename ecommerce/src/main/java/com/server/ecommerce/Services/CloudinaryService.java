package com.server.ecommerce.Services;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import com.server.ecommerce.DTO.Response.ImageUploadResponse;
import com.server.ecommerce.Entity.ImgPost;
import com.server.ecommerce.Entity.Posts;
import com.server.ecommerce.Exception.AppException;
import com.server.ecommerce.Exception.ErrorCode;
import com.server.ecommerce.Repository.PostImageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {
    private Cloudinary cloudinary;
    @Autowired
    private PostImageRepository postImageRepository;

    public CloudinaryService() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dm1tojcmv",
                "api_key", "694428189624197",
                "api_secret", "H1P7bbDsfP0sqqb0oDqVcDySuPQ"));
    }

    public ImageUploadResponse uploadImage(byte[] fileBytes) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(fileBytes, ObjectUtils.emptyMap());
        String secureUrl = (String) uploadResult.get("secure_url");
        String publicID = (String) uploadResult.get("public_id");

        return new ImageUploadResponse(publicID, secureUrl);
    }

    public void deleteImage(String publicID) throws IOException {
        cloudinary.uploader().destroy(publicID, ObjectUtils.emptyMap());

    }

    public List<ImageUploadResponse> imagetoCloudinary(List<MultipartFile> images, Posts post) throws IOException {
        try {
            List<ImageUploadResponse> imageUploadResponses = new ArrayList<>();
            for (MultipartFile image : images) {
                ImageUploadResponse imageUploadResponse = uploadImage(image.getBytes());
                String publicID = imageUploadResponse.getPublicID();
                String imgUrl = imageUploadResponse.getSecureUrl();
                ImgPost img = new ImgPost();
                img.setPublicID(publicID);
                img.setImgUrl(imgUrl);
                img.setPost(post);
                postImageRepository.save(img);
                imageUploadResponses.add(imageUploadResponse);
            }
            return imageUploadResponses;
        } catch (AppException exception) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }
}
