package edu.gcc.cement;
import io.javalin.Javalin;

import java.util.ArrayList;
import java.util.Map;

public class CalendarViewController {


    public static void registerRoutes(Javalin app) {

        app.get("/calendar", ctx -> {
            ctx.redirect("/pages/CalendarView.html");
        });

        //added these for the example course
        //THESE WILL BE REMOVED WHEN WE ADD A DATABASE

//        Time time1 = new Time("T", 9*60, 10*60); //*60 to convert to minutes
//        Time time2 = new Time("R", 9*60, 10*60);
//        ArrayList<Time> times1 = new ArrayList<>();
//        times1.add(time1);
//        times1.add(time2);
//
//        Time time3 = new Time("T", 11*60, 12*60);
//        Time time4 = new Time("R", 8*60, 9*60);
//        Time time5 = new Time("F", 9*60, 10*60);
//
//        ArrayList<Time> times2 = new ArrayList<>();
//        times2.add(time3);
//        times2.add(time4);
//        times2.add(time5);
//
//        ArrayList<String> professors = new ArrayList<String>();
//        professors.add("Graybill, Keith B.");



        //THIS IS HOW IT SHOULD WORK WHEN WE HAVE A DATABASE

        Schedule schedule = new Schedule("Fall");

        app.get("/api/schedule", ctx -> {
            try {
                ctx.json(schedule.getCourses());
            } catch (Exception e) {
                e.printStackTrace(); // this will show the error in the Java console
                ctx.status(500).result("Server Error: " + e.getMessage());
            }
        });

        //example courses to send to the calendar since we don't have a database
        //ALL HARD CODED THINGS WILL BE REMOVED ONCE WE HAVE A DATABASE

//        Course course1 = new Course("PRINCIPLES OF ACCOUNTING I", "ACCT 201", "A", "ACCT", professors, times1, "2023_Fall", "SHAL 316", 3, "");
//        Course course2 = new Course("Comp Sci Class", "COMP 240", "B", "COMP", professors, times2, "2023_Fall", "STEM 100", 3, "");
//        ArrayList<Course> courses = new ArrayList<>();
//        courses.add(course1);
//        courses.add(course2);

//        app.get("/api/schedule", ctx -> {
//            try {
//                ctx.json(courses);
//            } catch (Exception e) {
//                e.printStackTrace(); // this will show the error in the Java console
//                ctx.status(500).result("Server Error: " + e.getMessage());
//            }
//        });

        app.post("/api/add-course", ctx -> {

            Course newCourse = ctx.bodyAsClass(Course.class);

            schedule.getCourses().add(newCourse);

            ctx.result("Course added");

        });

        app.post("/api/remove-course", ctx -> {

            Map<String, String> data = ctx.bodyAsClass(Map.class);

            String name = data.get("name");
            String section = data.get("section");

            schedule.getCourses().removeIf(c ->
                    c.getName().equals(name) &&
                            c.getSection().equals(section)
            );

            ctx.result("Course removed");

        });


    }



}
