package com.server.ecommerce.DTO;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportDTO {
    private Long id;
    private Long accuser;
    private Long accused;
    private String reason;
    private LocalDateTime sendAt;
    private String status;
    private List<MultipartFile> images;
}
