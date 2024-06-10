package com.server.ecommerce.DTO;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ReportResponse {
    private Long id;
    private String accuser;
    private String accused;
    private String reason;
    private LocalDateTime sendAt;
    private Long postID;
    private String verifier;
    private LocalDateTime verifyAt;
    private String action;
    private String status;
    private String result;

}
