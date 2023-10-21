package com.server.ecommerce.Respository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.server.ecommerce.Entity.Message;

public interface MessageRespository extends JpaRepository<Message, Long> {
    List<Message> findBySenderIDAndReceiverIDOrSenderIDAndReceiverIDOrderBySendAt(Long senderID, Long receiverID,
            Long receiverID2, Long senderID2);

    @Query("SELECT DISTINCT CASE WHEN m.senderID = :userID THEN m.receiverID WHEN m.receiverID = :userID THEN m.senderID END FROM Message m WHERE m.senderID = :userID OR m.receiverID = :userID")
    List<Long> getMessageByUserID(Long userID);

    @Query("SELECT m FROM Message m WHERE (m.senderID = :user1 AND m.receiverID = :user2) OR (m.senderID = :user2 AND m.receiverID = :user1) ORDER BY m.sendAt DESC")
    List<Message> findLastMessageBetweenUser(Long user1, Long user2);

}
