package com.server.ecommerce.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReportResponse {
    private String id;
    private String accuser;
    private String accused;
    private String reason;
    private LocalDateTime sendAt;
    private String postID;
    private String verifier;
    private LocalDateTime verifyAt;
    private String action;
    private String status;
    private String result;

}
