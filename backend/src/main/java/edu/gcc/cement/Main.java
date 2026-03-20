package edu.gcc.cement;
import java.util.ArrayList;
import java.io.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


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
    public static void run(){
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.directory = "frontend";
                staticFiles.location = Location.EXTERNAL;
            });
        }).start(7000);

        CalendarViewController.registerRoutes(app);
        SearchController.registerRoutes(app);
    }
}
