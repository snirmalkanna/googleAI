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
public class GeminiChatController {

    private final ChatClient chatClient;

    public GeminiChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

String system = """
        Be specific and clear: The more detail you provide, the better the AI will understand your needs.
        Define your audience: Tell the AI who you are writing for (e.g., "small business owners") so it can tailor the language and complexity.
        Set the tone: Specify the desired tone, such as "friendly," "professional," or "funny".
        Include keywords: Mention any target keywords you want to be included in the content.
        Break it down: Consider generating an outline first, then asking the AI to expand on each section individually.""";

    @GetMapping("/generate")
    public String generate(@RequestParam(value = "prompt", defaultValue = "Tell me a joke") String prompt) {
        // Use the ChatClient API to call the model
        return this.chatClient.prompt()
                .user(p-> {
                    p.text(prompt);
                    p.param("topics", "technology,science");
                }).system(system)
                .call()
                .content(); // Extracts the response content as a String
    /*return this.chatClient.prompt()
                .user(prompt)
                .call()
                .content();*/ // Extracts the response content as a String
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "prompt", defaultValue = "Tell me a joke") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatClient.prompt(prompt).stream().chatResponse();
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody PromptRequest promptRequest) {
        try {
            String response = chatClient.prompt()
                    .user(promptRequest.getPrompt())
                    .call()
                    .content();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }
}
