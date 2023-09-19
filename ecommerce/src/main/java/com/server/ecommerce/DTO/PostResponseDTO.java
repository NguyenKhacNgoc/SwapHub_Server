package com.server.ecommerce.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostResponseDTO {
    private Long id;
    private String title;
    private String category;
    private String description;
    private Float price;
    private String email;
    private List<ImageUploadResponse> images;

}
