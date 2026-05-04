package edu.gcc.cement;

public class IntentRouter {

    public ChatIntent detectIntent(String message) {
        if (message == null || message.isBlank()) {
            return ChatIntent.UNKNOWN;
        }

        String lower = message.toLowerCase();

        if (lower.contains("how do i") || lower.contains("how to") || lower.contains("help")) {
            return ChatIntent.APP_HELP;
        }

        if (lower.contains("schedule") || lower.contains("build me") || lower.contains("suggest")) {
            return ChatIntent.SCHEDULE_SUGGESTION;
        }

        if (lower.contains("find") || lower.contains("search") || lower.contains("show me")) {
            return ChatIntent.COURSE_SEARCH;
        }



        return ChatIntent.UNKNOWN;
    }
}