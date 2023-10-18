package com.server.ecommerce.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.ecommerce.DTO.MessageDTO;
import com.server.ecommerce.Entity.Message;
import com.server.ecommerce.JWT.JwtTokenUtil;
import com.server.ecommerce.Respository.MessageRespository;
import com.server.ecommerce.Respository.UserRespository;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    @Autowired
    private MessageRespository messageRespository;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private UserRespository userRespository;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestHeader("Authorization") String authorization,
            @RequestBody MessageDTO request) {
        String token = authorization.substring(7);
        String email = jwtTokenUtil.getEmailFromToken(token);
        if (jwtTokenUtil.validateToken(token)) {
            Message message = new Message();
            message.setSenderID(userRespository.findByEmail(email).get().getId());
            message.setReceiverID(request.getReceiverID());
            message.setContent(request.getContent());
            message.setSendAt(request.getSendAt());
            messageRespository.save(message);

            return ResponseEntity.ok().build();
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestHeader("Authorization") String authorization,
            @RequestParam("receiverID") Long receiverID) {
        String token = authorization.substring(7);

        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            Long senderID = userRespository.findByEmail(email).get().getId();
            List<Message> messages = messageRespository.findAllBySenderIDAndReceiverIDOrReceiverIDAndSenderID(senderID,
                    receiverID, receiverID, senderID);
            return ResponseEntity.ok(messages);
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
