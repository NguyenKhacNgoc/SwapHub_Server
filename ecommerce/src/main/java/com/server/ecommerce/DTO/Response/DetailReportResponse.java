package com.server.ecommerce.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class DetailReportResponse {
    private ReportResponse reportResponse;
    private List<ImageUploadResponse> images;
}
