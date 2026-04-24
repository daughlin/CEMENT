package edu.gcc.cement;

import java.util.ArrayList;

public class ChatCourseSearchService {

    public ChatResponse handle(ChatRequest request) {
        try {
            String message = request.getMessage();
            String semester = request.getSemester();

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
            ChatSearchRequest chatRequest = parser.parse(message);

            ArrayList<Filter> filters = new ArrayList<>(chatRequest.getFilters());

            if (semester != null && !semester.isBlank()) {
                filters.add(new Filter(semester, Type.SEM));
            }

            Search actualSearch = new Search(
                    chatRequest.getQuery(),
                    filters,
                    courses
            );

            ArrayList<Course> results = actualSearch.getResults();

            String reply = summarizeResults(
                    results,
                    filters,
                    chatRequest.getQuery(),
                    semester
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
                                    String query,
                                    String semester) {

        StringBuilder sb = new StringBuilder();

        sb.append("Parsed query: ");
        if (query == null || query.isBlank()) {
            sb.append("none");
        } else {
            sb.append("\"").append(query).append("\"");
        }
        sb.append(". ");

        sb.append("Semester: ");
        if (semester == null || semester.isBlank()) {
            sb.append("none");
        } else {
            sb.append(semester);
        }
        sb.append(". ");

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