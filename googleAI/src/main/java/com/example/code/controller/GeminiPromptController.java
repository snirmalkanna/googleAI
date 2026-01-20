package com.example.code.controller;
import com.example.code.entity.PromptRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
public class GeminiPromptController {

    private final ChatClient chatClient;

    public GeminiPromptController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

String system = """
        Be specific and clear: The more detail you provide, the better the AI will understand your needs.
        Define your audience: Tell the AI who you are writing for (e.g., "small business owners") so it can tailor the language and complexity.
        Set the tone: Specify the desired tone, such as "friendly," "professional," or "funny".
        Include keywords: Mention any target keywords you want to be included in the content.
        Break it down: Consider generating an outline first, then asking the AI to expand on each section individually.""";

    @GetMapping("/generateprompt")
    public String generate(@RequestParam(value = "prompt", defaultValue = "write a blog of topic spring AI") String prompt) {
        // Use the ChatClient API to call the model
        return this.chatClient.prompt()
                .user(p-> {
                    p.text(prompt);
                    p.param("topics", "spring AI, spring boot, java");
                }).system(system)
                .call()
                .content();
    }
}
