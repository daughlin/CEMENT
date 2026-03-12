package edu.gcc.cement;

import io.javalin.Javalin;

import java.util.ArrayList;
import java.util.List;

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

            List<Course> results = newSearch.getResults();

            ctx.json(results);

        });
    }
}