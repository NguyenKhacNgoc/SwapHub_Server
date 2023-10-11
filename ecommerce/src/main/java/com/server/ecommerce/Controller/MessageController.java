package com.server.ecommerce.Controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import com.server.ecommerce.Entity.Message;

import com.server.ecommerce.JWT.JwtTokenUtil;
import com.server.ecommerce.Respository.MessageRespository;
import com.server.ecommerce.Respository.UserRespository;

@Controller
public class MessageController {
    @Autowired
    private MessageRespository messageRespository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRespository userRespository;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message sendMessage(@Payload Message message) {
        message.setSendAt(LocalDateTime.now());
        messageRespository.save(message);
        return message;
    }

    @MessageMapping("/chat/history")
    public List<Message> getChatHistory(Long senderID, Long receiverID) {
        return messageRespository.findAllBySenderIDAndReceiverIDOrReceiverIDAndSenderID(senderID, receiverID,
                receiverID, senderID);
    }

    @ResponseBody
    @GetMapping("/api/getuserID")
    public ResponseEntity<?> getUserID(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            Long userID = userRespository.findByEmail(email).get().getId();
            return ResponseEntity.ok(userID);

        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

    }

}
