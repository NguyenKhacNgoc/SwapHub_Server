package com.server.ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.server.ecommerce.Services.SSEServices;

@RestController
@RequestMapping("/api/sse")
public class SSEController {
    @Autowired
    private SSEServices sseServices;

    @GetMapping("/chat")
    public SseEmitter chatSSE() {
        return sseServices.createSSEEmitterForChat();
    }

    @GetMapping("/post")
    public SseEmitter createPost() {
        return sseServices.createSSEEmitterForPost();
    }

}
