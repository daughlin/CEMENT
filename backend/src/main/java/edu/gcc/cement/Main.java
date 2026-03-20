package edu.gcc.cement;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

/**
 * @author heisertd
 */
public class Main {
    public static void main(String[] args) {
        run();
    }

    /**
     * Function to run the program
     */
    public static void run() {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
                staticFiles.location = Location.CLASSPATH;
            });
        }).start(7000);

        CalendarViewController.registerRoutes(app);
        SearchController.registerRoutes(app);
    }
}