package com.server.ecommerce.Controller;

import java.util.ArrayList;
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
import com.server.ecommerce.DTO.MessageResponseDTO;

import com.server.ecommerce.DTO.ProfileMessageResponseDTO;
import com.server.ecommerce.Entity.Message;
import com.server.ecommerce.Entity.Profile;
import com.server.ecommerce.JWT.JwtTokenUtil;
import com.server.ecommerce.Respository.MessageRespository;
import com.server.ecommerce.Respository.ProfileRespository;
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
    @Autowired
    private ProfileRespository profileRespository;

    @PostMapping("/chat/send")
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

    @GetMapping("/chat/history")
    public ResponseEntity<?> getHistory(@RequestHeader("Authorization") String authorization,
            @RequestParam("receiverID") Long receiverID) {
        String token = authorization.substring(7);

        if (jwtTokenUtil.validateToken(token)) {
            String email = jwtTokenUtil.getEmailFromToken(token);
            Long senderID = userRespository.findByEmail(email).get().getId();
            List<Message> messages = messageRespository.findBySenderIDAndReceiverIDOrSenderIDAndReceiverIDOrderBySendAt(
                    senderID, receiverID, receiverID, senderID);
            List<MessageResponseDTO> messageResponseDTOs = new ArrayList<>();
            for (Message message : messages) {
                MessageResponseDTO messageResponseDTO = new MessageResponseDTO();
                messageResponseDTO.setMessage(message);
                if (message.getSenderID() == senderID)
                    messageResponseDTO.setStatus("send");
                else
                    messageResponseDTO.setStatus("receiver");

                messageResponseDTOs.add(messageResponseDTO);
            }
            return ResponseEntity.ok(messageResponseDTOs);
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/getMessageByUserID")
    public ResponseEntity<?> getMessageByUserID(@RequestHeader("Authorization") String authorization) {
        String token = authorization.substring(7);
        if (jwtTokenUtil.validateToken(token)) {
            Long userID = userRespository.findByEmail(jwtTokenUtil.getEmailFromToken(token)).get().getId();
            List<Long> list = messageRespository.getMessageByUserID(userID);
            List<ProfileMessageResponseDTO> profileMessageResponseDTOs = new ArrayList<>();
            for (Long id : list) {
                Profile profile = profileRespository.findById(id).get();
                ProfileMessageResponseDTO profileMessageResponseDTO = new ProfileMessageResponseDTO();
                profileMessageResponseDTO.setId(profile.getId());
                profileMessageResponseDTO.setPhoneNumber(profile.getPhoneNumber());
                profileMessageResponseDTO.setAddress(profile.getAddress());
                profileMessageResponseDTO.setDateofbirth(profile.getDateofbirth());
                profileMessageResponseDTO.setEmail(profile.getUser().getEmail());
                profileMessageResponseDTO.setFullName(profile.getFullName());
                profileMessageResponseDTO.setSex(profile.getSex());

                Message message = messageRespository.findLastMessageBetweenUser(profile.getId(), userID).get(0);
                MessageDTO messageDTO = new MessageDTO();
                messageDTO.setContent(message.getContent());
                messageDTO.setId(message.getId());
                messageDTO.setReceiverID(message.getReceiverID());
                messageDTO.setSendAt(message.getSendAt());
                messageDTO.setSenderID(message.getSenderID());
                profileMessageResponseDTO.setMessageDTO(messageDTO);
                if (userID == message.getSenderID())
                    profileMessageResponseDTO.setStatus("send");
                else
                    profileMessageResponseDTO.setStatus("receiver");
                profileMessageResponseDTOs.add(profileMessageResponseDTO);

            }
            return ResponseEntity.ok(profileMessageResponseDTOs);
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
