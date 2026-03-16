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

        // example code for how to use a search with filters
        // use "n credit(s)" format to search by credits
//        ArrayList<Filter> filters = new ArrayList<Filter>();
//        filters.add(new Filter("3", Type.CREDITS));
//        filters.add(new Filter("reli", Type.DEPT));
//        filters.add(new Filter("ansberry", Type.PROF));
//        try {
//            Search testSearch = new Search("", filters);
//            System.out.println("--------------------");
//            for (Course course : testSearch.getResults()) {
//                System.out.println(course.getName());
//                System.out.println(course.getCourseCode());
//                System.out.println(course.getProfessors().getFirst());
//                System.out.println(course.getCredits() + " credit(s)");
//                System.out.println("--------------------");
//            }
//        } catch ( Exception e) {
//            System.out.println(e.getMessage());
//        }


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
