package com.server.ecommerce.Services;

import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    public String uploadImage(byte[] fileBytes) throws IOException {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dm1tojcmv",
                "api_key", "694428189624197",
                "api_secret", "H1P7bbDsfP0sqqb0oDqVcDySuPQ"));

        Map<?, ?> uploadResult = cloudinary.uploader().upload(fileBytes, ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }
}
