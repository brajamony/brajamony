package com.example.mcpclient.controller;

import com.example.mcpclient.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Simple REST API for the chat interface.
 *
 * During the live demo you'll call this from the terminal with curl:
 *
 *   curl -s -X POST http://localhost:8081/chat \
 *        -H "Content-Type: application/json" \
 *        -d '{"message": "What is the price of SKU-001?"}'
 *
 * Or open the chat UI at: http://localhost:8081
 */
@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * POST /chat
     * Body: { "message": "your question here" }
     * Returns: { "question": "...", "answer": "..." }
     */
    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(
            @RequestBody Map<String, String> request) {

        String message = request.get("message");
        if (message == null || message.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Field 'message' is required"));
        }

        String answer = chatService.chat(message);
        return ResponseEntity.ok(Map.of(
                "question", message,
                "answer",   answer
        ));
    }

    /**
     * GET /health
     * Quick sanity-check that the client is up.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "service", "mcp-client"));
    }
}
