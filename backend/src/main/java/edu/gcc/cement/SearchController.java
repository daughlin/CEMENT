package edu.gcc.cement;
import io.javalin.Javalin;

public class SearchController {
    public static void registerRoutes(Javalin app) {
        app.get("/search", ctx -> {
            ctx.redirect("/Search.html");
        });
    }
}
