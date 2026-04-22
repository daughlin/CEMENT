package edu.gcc.cement;

import java.util.ArrayList;

public class ChatCourseSearchService {

    public ChatResponse handle(String message) {
        try {
            Search baseSearch = new Search("", new ArrayList<>());
            ArrayList<Course> courses = baseSearch.getCourseList();

            ChatFilterParser parser = new ChatFilterParser(courses);
            ChatSearchRequest chatRequest = parser.parse(message);

            Search actualSearch = new Search(chatRequest.getQuery(), chatRequest.getFilters(), courses);
            ArrayList<Course> results = actualSearch.getResults();

            String reply = summarizeResults(results);

            return new ChatResponse(reply, ChatIntent.COURSE_SEARCH.name(), results);

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse(
                    "I ran into an error while searching for courses.",
                    ChatIntent.COURSE_SEARCH.name(),
                    null
            );
        }
    }

    private String summarizeResults(ArrayList<Course> results) {
        if (results == null || results.isEmpty()) {
            return "I couldn’t find any courses matching that request.";
        }

        StringBuilder sb = new StringBuilder();
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