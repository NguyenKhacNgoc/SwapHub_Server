package com.server.ecommerce.Services;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.server.ecommerce.DTO.MessageDTO;

@Service
public class SSEServices {
    private final List<SseEmitter> chatEmitter = new CopyOnWriteArrayList<>();
    private List<SseEmitter> postEmitters = new CopyOnWriteArrayList<>();

    public SseEmitter createSSEEmitterForChat() {
        SseEmitter emitter = new SseEmitter();
        chatEmitter.add(emitter);
        emitter.onCompletion(() -> chatEmitter.remove(emitter));
        emitter.onTimeout(() -> chatEmitter.remove(emitter));
        return emitter;
    }

    public SseEmitter createSSEEmitterForPost() {
        SseEmitter emitter = new SseEmitter();
        postEmitters.add(emitter);
        emitter.onCompletion(() -> postEmitters.remove(emitter));
        emitter.onTimeout(() -> postEmitters.remove(emitter));
        return emitter;
    }

    public void sendChatMessage(MessageDTO message) {
        chatEmitter.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("message").data(message));
            } catch (IOException e) {
                // Handle errors or remove the emitter from the list.

            }
        });
    }

    public void updatePosts(String message) {
        postEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("post").data(message));
            } catch (IOException e) {
                // Handle errors or remove the emitter from the list.

            }

        });
    }
}