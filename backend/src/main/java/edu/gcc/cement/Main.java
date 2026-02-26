package edu.gcc.cement;
import java.util.ArrayList;
import java.io.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


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

        System.out.println("Working dir: " + System.getProperty("user.dir"));
        System.out.println("Absolute path: " + f.getAbsolutePath());
        System.out.println("Exists? " + f.exists());

        try {
            root = mapper.readTree(f);
            for (JsonNode c : root.get("classes")) {
                String name = c.path("name").asText();
                String dept = c.path("subject").asText();
                String number = c.path("number").asText();
                int credits = c.path("credits").asInt();
                String section = c.path("section").asText();
                //String professor;
                ArrayList<Time> times = parseTimes(c.path("times"));
                String semester = c.path("semester").asText();
                String location = c.path("location").asText();

                courses.add(new Course(name, dept + " " + number, section, dept, "test", times, semester, location, credits, ""));

            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }



        for (Course course : courses) {
            System.out.println(course.getName());
            System.out.println(course.getCourseCode());
            System.out.println(course.getDepartment());
            System.out.println(course.getSection());
            for (Time time : course.getTimes()) {
                System.out.println(time.getDay());
                System.out.println(time.getStartTime());
                System.out.println(time.getEndTime());
            }
            System.out.println("-----------------------------");
        }


    }

    /**
     * Function to run the program
     */
    public void run(){

    }

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