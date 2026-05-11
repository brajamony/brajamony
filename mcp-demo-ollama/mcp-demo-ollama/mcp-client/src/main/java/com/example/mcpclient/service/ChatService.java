package com.example.mcpclient.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * Chat service that routes user questions to the LLM,
 * making the MCP product catalogue tools available on every call.
 *
 * How it works:
 *  1. Spring AI auto-configures a ToolCallbackProvider bean containing all
 *     tools discovered from the connected MCP servers.
 *  2. We pass that provider to ChatClient on every call via .toolCallbacks().
 *  3. The LLM decides whether and when to call any of the tools.
 *  4. Spring AI executes the tool call against the MCP server and feeds the
 *     result back to the LLM to produce the final answer.
 *
 * You never write the dispatch loop — Spring AI handles it entirely.
 */
@Service
public class ChatService {

    private final ChatModel chatModel;
    private final ToolCallbackProvider mcpTools;

    // @Lazy on ChatClient prevents circular-dependency issues when the
    // ChatClient itself also depends on MCP components at startup.
    public ChatService(
            @Lazy ChatModel chatModel,
            @Autowired ToolCallbackProvider mcpTools) {
        this.chatModel = chatModel;
        this.mcpTools  = mcpTools;
    }

    /**
     * Send a user message to the LLM with the full product catalogue tool set available.
     *
     * @param userMessage Any natural-language question about the catalogue
     * @return The LLM's natural-language answer (may include tool-call results)
     */
    public String chat(String userMessage) {
        return ChatClient
                .create(chatModel)
                .prompt()
                .system("""
                    You are a helpful product catalogue assistant.
                    You have access to a product catalogue with Electronics, Furniture, and Books.
                    Use the available tools to look up accurate product information.
                    Always include the SKU, price, and stock level in your responses.
                    If a product is out of stock (stock level 0), say so clearly.
                    """)
                .user(userMessage)
                .toolCallbacks(mcpTools)
                .call()
                .content();
    }
}
