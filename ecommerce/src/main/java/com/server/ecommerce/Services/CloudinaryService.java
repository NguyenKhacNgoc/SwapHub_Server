package com.server.ecommerce.Services;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import com.server.ecommerce.DTO.ImageUploadResponse;
import com.server.ecommerce.Entity.Images;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CloudinaryService {
    private Cloudinary cloudinary;

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

    // Viết nhờ phương thức này ở đây, đáng lẽ phải viết ở service khác nhưng lười
    // tạo quá
    public List<ImageUploadResponse> copyImagesToImageUploadResponses(List<Images> images) {
        List<ImageUploadResponse> listImageUploadResponses = new ArrayList<>();
        for (Images img : images) {
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.setPublicID(img.getPublicID());
            imageUploadResponse.setSecureUrl(img.getImgUrl());
            listImageUploadResponses.add(imageUploadResponse);
        }
        return listImageUploadResponses;
    }
}
