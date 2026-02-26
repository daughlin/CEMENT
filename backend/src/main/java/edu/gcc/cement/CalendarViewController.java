package edu.gcc.cement;
import io.javalin.Javalin;
import java.util.Map;

public class CalendarViewController {

    public static void registerRoutes(Javalin app) {
        app.get("/calendar", ctx -> {
            ctx.redirect("/CalendarView.html");
        });
    }

}
