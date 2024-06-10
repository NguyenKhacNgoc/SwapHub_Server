package com.server.ecommerce.DTO;

import com.google.cloud.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private Long senderID;
    private Long receiverID;
    private String content;
    private Timestamp sendAt;
    private boolean seen;

}
