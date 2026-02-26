package edu.gcc.cement;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

/**
 * @author heisertd
 */

public class Main {
    public static void main(String[] args) {

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.directory = "../frontend/pages";
                staticFiles.location = Location.EXTERNAL;
            });
        }).start(7000);

        CalendarViewController.registerRoutes(app);
    }

    /**
     * Function to run the program
     */
    public static void run(){

    }

}
