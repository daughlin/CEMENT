package edu.gcc.cement;
import io.javalin.Javalin;

import java.util.ArrayList;
import java.util.Map;

public class CalendarViewController {


    public static void registerRoutes(Javalin app) {

        app.get("/calendar", ctx -> {
            ctx.redirect("/CalendarView.html");
        });

        //added these for the example course vvv

        Time time1 = new Time("T", 9, 10);
        Time time2 = new Time("R", 9, 10);
        ArrayList<Time> times = new ArrayList<>();
        times.add(time1);
        times.add(time2);

        ArrayList<String> professors = new ArrayList<String>();
        professors.add("Graybill, Keith B.");


        //Schedule schedule = new Schedule("Fall");
        //schedule.addCourse("ACCT 201");


        //THIS IS HOW IT SHOULD WORK WHEN WE HAVE A DATABASE
//        app.get("/api/schedule", ctx -> {
//            try {
//                ctx.json(schedule.getCourses());
//            } catch (Exception e) {
//                e.printStackTrace(); // this will show the error in the Java console
//                ctx.status(500).result("Server Error: " + e.getMessage());
//            }
//        });

        //example course to send to the calendar since we don't have a database

        Course course = new Course("PRINCIPLES OF ACCOUNTING I", "ACCT 201", "A", "ACCT", professors, times, "2023_Fall", "SHAL 316", 3, "");
        ArrayList<Course> courses = new ArrayList<>();
        courses.add(course);

        app.get("/api/schedule", ctx -> {
            try {
                ctx.json(courses);
            } catch (Exception e) {
                e.printStackTrace(); // this will show the error in the Java console
                ctx.status(500).result("Server Error: " + e.getMessage());
            }
        });



    }



}
