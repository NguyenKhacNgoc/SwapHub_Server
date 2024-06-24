package com.server.ecommerce.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class thongkeResponse {
    private Long user;
    private Long post;
    private Long vipham;

}
