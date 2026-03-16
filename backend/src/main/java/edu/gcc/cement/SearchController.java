package edu.gcc.cement;

import io.javalin.Javalin;
import java.util.*;

public class SearchController {

    public static void registerRoutes(Javalin app) {

        // Load the search page
        app.get("/search", ctx -> {
            ctx.redirect("/pages/Search.html");
        });

        // SEARCH API (handles query + filters)
        app.get("/api/search", ctx -> {

            String query = ctx.queryParam("q");

            ArrayList<Filter> filters = new ArrayList<>();

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

            String days = ctx.queryParam("days");
            if (days != null && !days.isBlank()) {
                filters.add(new Filter(days, Type.DAYS));
            }

            Search search = new Search(query, filters);

            ArrayList<Course> results = search.getResults();

            ctx.json(results);
        });

        // FILTER OPTIONS API (dynamic dropdowns)
        app.get("/api/filters", ctx -> {

            Search search = new Search("", new ArrayList<>());

            ArrayList<Course> courses = search.getResults();

            HashSet<String> departments = new HashSet<>();
            HashSet<String> professors = new HashSet<>();
            HashSet<Integer> credits = new HashSet<>();
            HashSet<String> days = new HashSet<>();

            for (Course c : courses) {

                departments.add(c.getDepartment());
                credits.add(c.getCredits());

                for (String prof : c.getProfessors()) {
                    professors.add(prof);
                }

                for (Time t : c.getTimes()) {
                    days.add(t.getDay());
                }
            }

            Map<String,Object> data = new HashMap<>();

            data.put("departments", departments);
            data.put("professors", professors);
            data.put("credits", credits);
            data.put("days", days);

            ctx.json(data);
        });
    }
}