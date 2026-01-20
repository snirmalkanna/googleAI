package com.example.code.controller;

import org.springframework.ai.chat.client.ResponseEntity;
import org.springframework.ai.image.ImageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GeminiMultimodalController {


    private final ChatClient chatClient;

    @Value("classpath:/images/51237-Niagara-Falls-New-York.jpg")
    Resource resource;

    public GeminiMultimodalController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/generateMultimodal")
    public String generateMultimodal() {
        return this.chatClient.prompt()
                .user(u -> {
                    u.text("Describe the image in detail.");
                    u.media(MimeTypeUtils.IMAGE_JPEG, resource);
                })
                .call()
                .content();
    }

}
