package com.example.code.controller;
import com.example.code.entity.Itinerary;
import com.example.code.entity.PromptRequest;
import com.example.code.entity.Story;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
public class GeminiStructuredController {

    private final ChatClient chatClient;

    public GeminiStructuredController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }


    @GetMapping("/generateStructured")
    public Itinerary generateStructured() {
        return this.chatClient.prompt()
                .user("I want to plan a trip to switzerland.Give me a list of things to do")
                .call()
                .entity(Itinerary.class);
    }

    @GetMapping("/generateStory")
    public Story generateStory(@RequestParam(value = "prompt", defaultValue = "Tell me a joke") String message) {
        return this.chatClient.prompt()
                .user(message)
                .call()
                .entity(Story.class);
    }
}
