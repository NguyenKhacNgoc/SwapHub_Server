package com.server.ecommerce.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import com.server.ecommerce.DTO.DetailReportResponse;
import com.server.ecommerce.DTO.ImageUploadResponse;
import com.server.ecommerce.DTO.PostResponseDTO;
import com.server.ecommerce.DTO.ProfileDTO;
import com.server.ecommerce.DTO.ReportResponse;
import com.server.ecommerce.Entity.ImgPost;
import com.server.ecommerce.Entity.ImgReport;
import com.server.ecommerce.Entity.Posts;
import com.server.ecommerce.Entity.Report;
import com.server.ecommerce.Entity.User;
import com.server.ecommerce.Respository.ImageRespository;
import com.server.ecommerce.Respository.ImgReportRespository;

@Service
public class ResponseServices {
    @Autowired
    private ImageRespository imageRespository;
    @Autowired
    private ImgReportRespository imgReportRespository;

    public PostResponseDTO postResponse(Posts post) {
        PostResponseDTO postResponseDTO = new PostResponseDTO();
        postResponseDTO.setId(post.getId());
        postResponseDTO.setTitle(post.getTitle());
        postResponseDTO.setCategory(post.getCategory().getName());
        postResponseDTO.setPrice(post.getPrice());
        postResponseDTO.setStatus(post.getStatus());
        postResponseDTO.setDescription(post.getDescription());
        postResponseDTO.setPostAt(post.getPostAt());
        // Profile
        postResponseDTO.setProfile(responseProfileDTO(post.getUser()));
        List<ProfileDTO> likedBys = new ArrayList<>();
        List<User> userlikeBys = post.getLikedBy();
        for (User user : userlikeBys) {
            likedBys.add(responseProfileDTO(user));

        }
        postResponseDTO.setLikedBy(likedBys);

        // Cái cloudinaryService này viết nhờ phương thức thôi, tại lười
        postResponseDTO.setImages(copyImagesPostToImageUploadResponses(imageRespository.findByPost(post)));
        return postResponseDTO;

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

    public List<ImageUploadResponse> copyImagesReportToImageUploadResponses(List<ImgReport> images) {
        List<ImageUploadResponse> listImageUploadResponses = new ArrayList<>();
        for (ImgReport img : images) {
            ImageUploadResponse imageUploadResponse = new ImageUploadResponse();
            imageUploadResponse.setPublicID(img.getPublicID());
            imageUploadResponse.setSecureUrl(img.getImgUrl());
            listImageUploadResponses.add(imageUploadResponse);
        }
        return listImageUploadResponses;
    }

    public ProfileDTO responseProfileDTO(User user) {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setDateofbirth(user.getDateofbirth());
        profileDTO.setDistrict(user.getAddress().getDistrict());
        profileDTO.setEmail(user.getEmail());
        profileDTO.setFullName(user.getFullName());
        profileDTO.setId(user.getId());
        profileDTO.setPhoneNumber(user.getPhoneNumber());
        profileDTO.setProvince(user.getAddress().getProvince());
        profileDTO.setSex(user.getSex());
        profileDTO.setStatus(user.getStatus());
        profileDTO.setWard(user.getAddress().getWard());
        return profileDTO;
    }

    public ReportResponse responseReport(Report report) {
        ReportResponse reportResponse = new ReportResponse();
        reportResponse.setId(report.getId());
        reportResponse.setAccuser(report.getAccuser().getEmail());
        reportResponse.setAccused(report.getAccused().getEmail());
        reportResponse.setReason(report.getReason());
        reportResponse.setSendAt(report.getSendAt());
        reportResponse.setPostID(report.getPost().getId());
        reportResponse.setStatus(report.getStatus());
        reportResponse.setVerifier("UnKnown");
        reportResponse.setVerifyAt(report.getVerifyAt());
        reportResponse.setResult(report.getResult());
        reportResponse.setAction(report.getAction());
        if (report.getVerifier() != null) {
            reportResponse.setVerifier(report.getVerifier().getEmail());
        }
        return reportResponse;
    }

    public DetailReportResponse responseDetailReport(Report report) {
        DetailReportResponse detailReportResponse = new DetailReportResponse();
        detailReportResponse.setReportResponse(responseReport(report));
        detailReportResponse
                .setImages(copyImagesReportToImageUploadResponses(imgReportRespository.findByReport(report)));

        return detailReportResponse;
    }
}
