package edu.gcc.cement;

public class ChatService {

    private final IntentRouter intentRouter;
    private final HelpService helpService;
    private final ChatCourseSearchService chatCourseSearchService;
    private final ScheduleSuggestionService scheduleSuggestionService;

    public ChatService() {
        this.intentRouter = new IntentRouter();
        this.helpService = new HelpService();
        this.chatCourseSearchService = new ChatCourseSearchService();
        this.scheduleSuggestionService = new ScheduleSuggestionService();
    }

    public ChatResponse handleMessage(ChatRequest request) {
        if (request == null || request.getMessage() == null || request.getMessage().isBlank()) {
            return new ChatResponse(
                    "Please enter a message.",
                    ChatIntent.UNKNOWN.name(),
                    null
            );
        }

        String message = request.getMessage();
        ChatIntent intent = intentRouter.detectIntent(message);

        return switch (intent) {
            case APP_HELP -> handleAppHelp(message);
            case COURSE_SEARCH -> handleCourseSearch(request);
            case SCHEDULE_SUGGESTION -> handleScheduleSuggestion(request);
            default -> new ChatResponse(
                    "I’m not sure what you mean yet. Try asking for help, course search, or a schedule suggestion.",
                    ChatIntent.UNKNOWN.name(),
                    null
            );
        };
    }

    private ChatResponse handleAppHelp(String message) {
        String reply = helpService.getHelpResponse(message);
        return new ChatResponse(reply, ChatIntent.APP_HELP.name(), null);
    }

    private ChatResponse handleCourseSearch(ChatRequest request) {
        return chatCourseSearchService.handle(request);
    }

    private ChatResponse handleScheduleSuggestion(ChatRequest request) {
        return scheduleSuggestionService.handle(request);
    }
}