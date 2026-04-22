package edu.gcc.cement;

import java.util.ArrayList;

public class ChatCourseSearchService {

    public ChatResponse handle(String message) {
        try {
            Search baseSearch = new Search("", new ArrayList<>());
            ArrayList<Course> courses = baseSearch.getCourseList();

            if (courses == null) {
                return new ChatResponse(
                        "I could not load the course list.",
                        ChatIntent.COURSE_SEARCH.name(),
                        null
                );
            }

            ChatFilterParser parser = new ChatFilterParser(courses);

            // 👇 NEW: use richer parser output
            ChatSearchRequest chatRequest = parser.parse(message);

            Search actualSearch = new Search(
                    chatRequest.getQuery(),
                    chatRequest.getFilters(),
                    courses
            );

            ArrayList<Course> results = actualSearch.getResults();

            String reply = summarizeResults(
                    results,
                    chatRequest.getFilters(),
                    chatRequest.getQuery()
            );

            return new ChatResponse(
                    reply,
                    ChatIntent.COURSE_SEARCH.name(),
                    results
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse(
                    "I ran into an error while searching for courses.",
                    ChatIntent.COURSE_SEARCH.name(),
                    null
            );
        }
    }

    private String summarizeResults(ArrayList<Course> results,
                                    ArrayList<Filter> filters,
                                    String query) {

        StringBuilder sb = new StringBuilder();

        // ✅ Show parsed query
        sb.append("Parsed query: ");
        if (query == null || query.isBlank()) {
            sb.append("none");
        } else {
            sb.append("\"").append(query).append("\"");
        }
        sb.append(". ");

        // ✅ Show parsed filters
        sb.append("Parsed filters: ");
        if (filters == null || filters.isEmpty()) {
            sb.append("none");
        } else {
            for (int i = 0; i < filters.size(); i++) {
                Filter f = filters.get(i);
                sb.append("[").append(f.getType()).append(": ").append(f.getValue()).append("]");
                if (i < filters.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append(". ");

        // ✅ Show results
        if (results == null || results.isEmpty()) {
            sb.append("I couldn’t find any courses matching that request.");
            return sb.toString();
        }

        sb.append("I found ").append(results.size()).append(" matching courses. ");

        int limit = Math.min(3, results.size());
        sb.append("Examples: ");

        for (int i = 0; i < limit; i++) {
            Course c = results.get(i);
            sb.append(c.getCourseCode()).append(" - ").append(c.getName());

            if (i < limit - 1) {
                sb.append("; ");
            }
        }

        sb.append(".");
        return sb.toString();
    }
}