package com.server.ecommerce.DTO;

import com.server.ecommerce.Entity.Message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseDTO {
    private String status;
    private Message message;
}
