package com.server.ecommerce.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostUpdateDTO {
    private Long id;
    private String title;
    private String description;
    private Float price;
}
