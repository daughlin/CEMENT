package edu.gcc.cement;

public class ChatService {

    private final IntentRouter intentRouter;
    private final HelpService helpService;

    public ChatService() {
        this.intentRouter = new IntentRouter();
        this.helpService = new HelpService();
    }

    public ChatResponse handleMessage(ChatRequest request) {
        String message = request.getMessage();
        ChatIntent intent = intentRouter.detectIntent(message);

        return switch (intent) {
            case APP_HELP -> handleAppHelp(message);
            case COURSE_SEARCH -> handleCourseSearch(message);
            case SCHEDULE_SUGGESTION -> handleScheduleSuggestion(message);
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

    private ChatResponse handleFilterExplanation(String message) {
        String reply = "Filters let you narrow courses by department, professor, credits, days, and time.";
        return new ChatResponse(reply, ChatIntent.FILTER_EXPLANATION.name(), null);
    }

    private ChatResponse handleCourseSearch(String message) {
        String reply = "Course search is not connected yet, but this is where backend search logic will run.";
        return new ChatResponse(reply, ChatIntent.COURSE_SEARCH.name(), null);
    }

    private ChatResponse handleScheduleSuggestion(String message) {
        String reply = "Schedule suggestions are not connected yet, but this is where schedule generation logic will run.";
        return new ChatResponse(reply, ChatIntent.SCHEDULE_SUGGESTION.name(), null);
    }
}