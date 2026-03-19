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

        // example code for how to load a saved schedule
//        Schedule schedule = ScheduleStorage.loadSchedule();
        //System.out.println("Current Schedule:");
//        System.out.println("--------------------");
//        for (Course course : schedule.getCourses()) {
//            System.out.println(course.getName());
//            System.out.println(course.getCourseCode());
//            System.out.println(course.getProfessors().getFirst());
//            System.out.println("--------------------");
//        }

            // example code for how to use a search with filters
            // use "n credit(s)" format to search by credits
//            ArrayList<Filter> filters = new ArrayList<Filter>();
//            filters.add(new Filter("3", Type.CREDITS));
//            filters.add(new Filter("reli", Type.DEPT));
//            filters.add(new Filter("ansberry", Type.PROF));
//            filters.add(new Filter("9:00", Type.START));
//            filters.add(new Filter("12:00", Type.END));
//            filters.add(new Filter("MWF", Type.DAYS));
//            try {
//                Search testSearch = new Search("", filters);
//                System.out.println("--------------------");
//                System.out.println(testSearch.getResults().size());
//                for (Course course : testSearch.getResults()) {
//                    System.out.println(course.getName());
//                    System.out.println(course.getCourseCode());
//                    System.out.println(course.getProfessors().getFirst());
//                    System.out.println(course.getCredits() + " credit(s)");
//                    //System.out.println(course.getDays());
//                    System.out.println("" + course.getDays() + ": " + course.getNiceTime());
//                    //System.out.println();
//                    System.out.println("--------------------");
//
////                     adding everything in search results to saved schedule as example
////                     call ScheduleStorage.saveSchedule(schedule) every time something is added or removed from the schedule
//                    //schedule.addCourse(course);
//                    //ScheduleStorage.saveSchedule(schedule);
//                }
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.directory = "frontend";
                staticFiles.location = Location.EXTERNAL;
            });
        }).start(7000);

        CalendarViewController.registerRoutes(app);
        SearchController.registerRoutes(app);
    }

    /**
     * Function to run the program
     */
    public static void run(){

    }
}
