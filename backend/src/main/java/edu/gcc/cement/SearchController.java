package edu.gcc.cement;

import io.javalin.Javalin;
import java.util.*;

public class SearchController {

    public static void registerRoutes(Javalin app) {

        // Redirect root to search
        app.get("/", ctx -> ctx.redirect("/pages/SemesterChoice.html"));

        // Load the search page
        app.get("/search", ctx -> {
            String queryString = ctx.queryString();

            if (queryString != null && !queryString.isBlank()) {
                ctx.redirect("/pages/Search.html?" + queryString);
            } else {
                ctx.redirect("/pages/Search.html");
            }
        });

        // SEARCH API (handles query + filters)
        app.get("/api/search", ctx -> {

            String query = ctx.queryParam("q");
            if (query == null) {
                query = "";
            }

            ArrayList<Filter> filters = new ArrayList<>();

            String semester = ctx.queryParam("semester");
            if (semester != null && !semester.isBlank()) {
                filters.add(new Filter(semester, Type.SEM));
            }

            String dept = ctx.queryParam("dept");
            if (dept != null && !dept.isBlank()) {
                filters.add(new Filter(dept, Type.DEPT));
            }

            String prof = ctx.queryParam("prof");
            if (prof != null && !prof.isBlank()) {
                filters.add(new Filter(prof, Type.PROF));
            }

            String credits = ctx.queryParam("credits");
            if (credits != null && !credits.isBlank()) {
                filters.add(new Filter(credits, Type.CREDITS));
            }

            String daysParam = ctx.queryParam("days");
            if (daysParam != null && !daysParam.isBlank()) {
                filters.add(new Filter(daysParam, Type.DAYS));
            }

            String startTime = ctx.queryParam("start");
            if (startTime != null && !startTime.isBlank()) {
                filters.add(new Filter(startTime, Type.START));
            }

            String endTime = ctx.queryParam("end");
            if (endTime != null && !endTime.isBlank()) {
                filters.add(new Filter(endTime, Type.END));
            }

            Search search = new Search(query, filters);

            ArrayList<Course> results = search.getResults();

            ctx.json(results);
        });

        // Inside SearchController.java, within registerRoutes method:

        // API to get all unique semesters from the course list
        app.get("/api/semesters", ctx -> {
            try {
                // Create a blank search to load all courses from the JSON file
                Search search = new Search("", new ArrayList<>());
                ArrayList<Course> courses = search.getResults();

                // Use a TreeSet to store unique semesters in alphabetical/chronological order
                Set<String> semesters = new TreeSet<>();
                for (Course c : courses) {
                    if (c.getSemester() != null && !c.getSemester().isBlank()) {
                        semesters.add(c.getSemester());
                    }
                }

                // Return the list as JSON
                ctx.json(new ArrayList<>(semesters));
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Error loading semesters: " + e.getMessage());
            }
        });

        // FILTER OPTIONS API (dynamic dropdowns)
        app.get("/api/filters", ctx -> {

            Search search = new Search("", new ArrayList<>());

            ArrayList<Course> courses = search.getResults();

            HashSet<String> departments = new HashSet<>();
            HashSet<String> professors = new HashSet<>();
            TreeSet<Integer> credits = new TreeSet<>();
            HashSet<String> days = new HashSet<>();
            Set<Integer> startTimes = new TreeSet<>();
            Set<Integer> endTimes = new TreeSet<>();

            for (Course c : courses) {

                departments.add(c.getDepartment());
                credits.add(c.getCredits());

                for (String prof : c.getProfessors()) {
                    professors.add(prof);
                }

                for (Time t : c.getTimes()) {
                    days.add(t.getDay());

                    startTimes.add(t.getStartTime());
                    endTimes.add(t.getEndTime());
                }
            }

            Map<String, Object> data = new HashMap<>();

            data.put("departments", departments);
            data.put("professors", professors);
            data.put("credits", credits);
            data.put("days", days);
            data.put("startTimes", startTimes);
            data.put("endTimes", endTimes);

            ctx.json(data);
        });
    }
}