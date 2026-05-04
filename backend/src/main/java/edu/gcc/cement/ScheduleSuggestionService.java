package edu.gcc.cement;

import java.util.ArrayList;

public class ScheduleSuggestionService {

    public ChatResponse handle(ChatRequest request) {
        try {
            String message = request.getMessage();
            String semester = request.getSemester();
            String major = request.getMajor();

            Degree degree = null;
            if (major != null && !major.isBlank()) {
                degree = DegreeLoader.loadDegree(major);
            }

            Search baseSearch = new Search("", new ArrayList<>());
            ArrayList<Course> allCourses = baseSearch.getCourseList();

            ChatFilterParser parser = new ChatFilterParser(allCourses);
            ChatSearchRequest parsed = parser.parse(message);

            ArrayList<Filter> filters = new ArrayList<>(parsed.getFilters());

            if (semester != null && !semester.isBlank()) {
                filters.add(new Filter(semester, Type.SEM));
            }

            Search filteredSearch = new Search(
                    parsed.getQuery(),
                    filters,
                    degree
            );

            ArrayList<Course> candidateCourses = filteredSearch.getResults();

            Schedule currentSchedule = CalendarViewController.getSchedule();
            ArrayList<Course> scheduledCourses = currentSchedule.getCourses();

            ArrayList<Course> fittingCourses = new ArrayList<>();

            for (Course candidate : candidateCourses) {
                if (isAlreadyScheduled(candidate, scheduledCourses)) {
                    continue;
                }

                if (!conflictsWithSchedule(candidate, scheduledCourses)) {
                    fittingCourses.add(candidate);
                }
            }

            return new ChatResponse(
                    summarize(fittingCourses, parsed.getQuery(), filters),
                    ChatIntent.SCHEDULE_SUGGESTION.name(),
                    fittingCourses
            );

        } catch (Exception e) {
            e.printStackTrace();
            return new ChatResponse(
                    "I ran into an error while finding courses that fit your schedule.",
                    ChatIntent.SCHEDULE_SUGGESTION.name(),
                    null
            );
        }
    }

    private boolean isAlreadyScheduled(Course candidate, ArrayList<Course> schedule) {
        for (Course scheduled : schedule) {
            if (candidate.getName().equals(scheduled.getName())
                    && candidate.getSection().equals(scheduled.getSection())) {
                return true;
            }
        }
        return false;
    }

    private boolean conflictsWithSchedule(Course candidate, ArrayList<Course> schedule) {
        for (Course scheduled : schedule) {
            if (coursesConflict(candidate, scheduled)) {
                return true;
            }
        }
        return false;
    }

    private boolean coursesConflict(Course a, Course b) {
        for (Time ta : a.getTimes()) {
            for (Time tb : b.getTimes()) {
                if (!ta.getDay().equals(tb.getDay())) {
                    continue;
                }

                boolean overlap =
                        ta.getStartTime() < tb.getEndTime()
                                && tb.getStartTime() < ta.getEndTime();

                if (overlap) {
                    return true;
                }
            }
        }

        return false;
    }

    private String summarize(ArrayList<Course> courses, String query, ArrayList<Filter> filters) {
        StringBuilder sb = new StringBuilder();

        sb.append("Parsed query: ");
        if (query == null || query.isBlank()) {
            sb.append("none");
        } else {
            sb.append("\"").append(query).append("\"");
        }

        sb.append(". Parsed filters: ");
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

        if (courses == null || courses.isEmpty()) {
            sb.append("I couldn’t find any courses that fit your current schedule.");
            return sb.toString();
        }

        sb.append("I found ")
                .append(courses.size())
                .append(" courses that fit your current schedule. ");

        int limit = Math.min(5, courses.size());
        sb.append("Some options: ");

        for (int i = 0; i < limit; i++) {
            Course c = courses.get(i);
            sb.append(c.getCourseCode())
                    .append(" - ")
                    .append(c.getName())
                    .append(" (")
                    .append(c.getSection())
                    .append(")");

            if (i < limit - 1) {
                sb.append("; ");
            }
        }

        sb.append(".");
        return sb.toString();
    }
}