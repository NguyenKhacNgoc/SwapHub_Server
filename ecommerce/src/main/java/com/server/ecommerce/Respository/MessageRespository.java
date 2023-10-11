package com.server.ecommerce.Respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.ecommerce.Entity.Message;

public interface MessageRespository extends JpaRepository<Message, Long> {
    List<Message> findAllBySenderIDAndReceiverIDOrReceiverIDAndSenderID(Long senderID1, Long receiverID1,
            Long receiverID2, Long senderID2);

}
