package com.server.ecommerce.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PostResponseDTO {
    private String id;
    private String title;
    private String category;
    private String description;
    private Float price;
    private String status;
    private UserDTOResponse user;
    private List<ImageUploadResponse> images;
    private List<UserDTOResponse> likedBy;
    private LocalDateTime postAt;

}
