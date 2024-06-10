package com.server.ecommerce.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import com.server.ecommerce.DTO.MessageDTO;

@RestController
@RequestMapping("/api/firebase")
public class FirebaseController {

    @GetMapping("/getfirebase")
    public ResponseEntity<?> getAllMessages() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        CollectionReference messagesRef = db.collection("message");
        ApiFuture<QuerySnapshot> future = messagesRef.get();

        List<MessageDTO> messages = new ArrayList<>();
        QuerySnapshot documents = future.get();
        for (DocumentSnapshot document : documents) {
            MessageDTO message = document.toObject(MessageDTO.class);
            messages.add(message);
        }

        return ResponseEntity.ok(messages);
    }

}
