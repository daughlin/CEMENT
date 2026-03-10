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
        ArrayList<Course> courses = new ArrayList<Course>();

        String classFile = "./backend/src/main/resources/data_wolfe.json";

        File f = new File(classFile);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;

        try {
            root = mapper.readTree(f);

            // building the courses from the json
            for (JsonNode c : root.get("classes")) {
                String name = c.path("name").asText();
                String dept = c.path("subject").asText();
                String number = c.path("number").asText();
                int credits = c.path("credits").asInt();
                String section = c.path("section").asText();

                ArrayList<String> professors = new ArrayList<String>();
                for(JsonNode prof : c.path("faculty")) {
                    professors.add(prof.asText(""));
                }
                ArrayList<Time> times = parseTimes(c.path("times"));
                String semester = c.path("semester").asText();
                String location = c.path("location").asText();

                courses.add(new Course(name, dept + " " + number, section, dept, professors, times, semester, location, credits, ""));

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(courses.get(10).getProfessors().get(0));

        // example code for how to use a search without filters
        // use "n credit(s)" format to search by credits
//        Search testSearch = new Search("3 credits", new ArrayList<Filter>(), courses);
//        for (Course course : testSearch.getResults()) {
//            System.out.println(course.getName());
//            System.out.println(course.getCourseCode());
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

    /**
     * Helper function to parse class times by day, start time, and end time from the json file
     * @param timesNode
     * @return ArrayList of time objects for the Course class
     */
    private static ArrayList<Time> parseTimes(JsonNode timesNode) {
        ArrayList<Time> times = new ArrayList<>();
        if (timesNode == null || !timesNode.isArray()) return times;

        for (JsonNode t : timesNode) {
            String day = t.path("day").asText("");
            int start = toMinutes(t.path("start_time").asText(""));
            int end   = toMinutes(t.path("end_time").asText(""));

            if (!day.isBlank() && start >= 0 && end >= 0) {
                times.add(new Time(day, start, end));
            }
        }
        return times;
    }

    /**
     * Helper function to convert time from HH:MM:SS format to minutes from midnight
     * @param hhmmss
     * @return Time converted to minutes from midnight
     */
    private static int toMinutes(String hhmmss) {
        if (hhmmss == null || hhmmss.isBlank()) return -1;
        String[] parts = hhmmss.split(":");
        if (parts.length < 2) return -1;

        try {
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            return h * 60 + m;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}
