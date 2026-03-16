package edu.gcc.cement;

import io.javalin.Javalin;

import java.util.*;

public class SearchController {

    public static void registerRoutes(Javalin app) {

        // load the search page
        app.get("/search", ctx -> {
            ctx.redirect("/pages/Search.html");
        });

        // search API
        app.get("/api/search", ctx -> {

            String query = ctx.queryParam("q");

            Search newSearch = new Search(query, new ArrayList<>());

            ArrayList<Course> results = newSearch.getResults();

            ctx.json(results);

        });

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

            ArrayList<Filter> filters = new ArrayList<>();

            String dept = ctx.queryParam("dept");
            if (dept != null && !dept.isBlank()) {
                filters.add(new Filter(dept, Type.DEPT));
            }

            String prof = ctx.queryParam("prof");
            if (prof != null && !prof.isBlank()) {
                filters.add(new Filter(prof, Type.PROF));
            }

            String creds = ctx.queryParam("creds");
            if (creds != null && !prof.isBlank()) {
                filters.add(new Filter(creds, Type.CREDITS));
            }

            String day = ctx.queryParam("days");
            if (day != null && !day.isBlank()) {
                filters.add(new Filter(day, Type.DAYS));
            }

            ctx.json(data);
        });
    }
}