package com.server.ecommerce.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private Long id;
    private Long senderID;
    private Long receiverID;
    private String content;
    private LocalDateTime timestamp;

}
