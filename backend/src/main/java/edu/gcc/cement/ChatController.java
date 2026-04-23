package edu.gcc.cement;

import io.javalin.Javalin;

public class ChatController {

    private static final ChatService chatService = new ChatService();

    public static void registerRoutes(Javalin app) {
        app.post("/api/chat", ctx -> {
            try {
                ChatRequest request = ctx.bodyAsClass(ChatRequest.class);
                ChatResponse response = chatService.handleMessage(request);
                ctx.json(response);
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Chat route error: " + e.getMessage());
            }
        });
    }
}