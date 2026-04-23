package edu.gcc.cement;

import io.javalin.Javalin;

import java.util.ArrayList;
import java.util.Map;

public class CalendarViewController {

    private static final Schedule schedule = buildInitialSchedule();

    public static void registerRoutes(Javalin app) {

        app.get("/calendar", ctx -> {
            ctx.redirect("/pages/CalendarView.html");
        });

        app.get("/schedule", ctx -> {
            try {
                ctx.json(schedule.getCourses());
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Server Error: " + e.getMessage());
            }
        });

        app.post("/schedule/courses", ctx -> {
            try {
                Course newCourse = ctx.bodyAsClass(Course.class);

                if (schedule.containsCourse(newCourse)) {
                    ctx.status(200).result("Course already exists");
                    return;
                }

                schedule.addCourse(newCourse);
                ctx.status(200).result("Course added");

            } catch (CourseTimeConflictsException e) {
                ctx.status(409).result(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Internal Server Error");
            }
        });

        app.delete("/schedule/courses", ctx -> {
            try {
                Map<String, String> data = ctx.bodyAsClass(Map.class);

                String name = data.get("name");
                String section = data.get("section");

                schedule.removeCourseByNameAndSection(name, section);

                ctx.result("Course removed");
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Internal Server Error");
            }
        });

        app.get("/favorites", ctx -> {
            try {
                ctx.json(schedule.getFavorites());
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Server Error: " + e.getMessage());
            }
        });

        app.post("/favorites", ctx -> {
            try {
                Course newCourse = ctx.bodyAsClass(Course.class);

                boolean added = schedule.favoriteCourse(newCourse);

                if (!added) {
                    ctx.result("Course already favorited");
                    return;
                }

                ctx.result("Course favorited");
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Server Error");
            }
        });

        app.delete("/favorites", ctx -> {
            Map<String, String> data = ctx.bodyAsClass(Map.class);

            String name = data.get("name");
            String section = data.get("section");

            schedule.unfavoriteCourse(name, section);

            ctx.result("Course unfavorited");
        });

        app.patch("/schedule/courses/color", ctx -> {
            try {
                Map<String, String> data = ctx.bodyAsClass(Map.class);

                String name = data.get("name");
                String section = data.get("section");
                String color = data.get("color");

                schedule.updateCourseColor(name, section, color);

                ctx.result("Course color updated");
            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Internal Server Error");
            }
        });
    }

    private static Schedule buildInitialSchedule() {
        Schedule schedule = new Schedule("2023_Fall");

        ArrayList<String> professors = new ArrayList<>();
        professors.add("Graybill, Keith B.");

        ArrayList<Time> times1 = new ArrayList<>();
        times1.add(new Time("T", 9 * 60, 10 * 60));
        times1.add(new Time("R", 9 * 60, 10 * 60));

        ArrayList<Time> times2 = new ArrayList<>();
        times2.add(new Time("T", 11 * 60, 12 * 60));
        times2.add(new Time("R", 8 * 60, 9 * 60));
        times2.add(new Time("F", 9 * 60, 10 * 60));

        ArrayList<Time> favoriteTimes = new ArrayList<>();
        favoriteTimes.add(new Time("T", 9 * 60, 10 * 60));
        favoriteTimes.add(new Time("R", 9 * 60, 10 * 60));

        try {
            schedule.addCourse(new Course(
                    "PRINCIPLES OF ACCOUNTING I",
                    "ACCT 201",
                    "A",
                    "ACCT",
                    professors,
                    times1,
                    "2023_Fall",
                    "SHAL 316",
                    3,
                    ""
            ));

            schedule.addCourse(new Course(
                    "Comp Sci Class",
                    "COMP 240",
                    "B",
                    "COMP",
                    professors,
                    times2,
                    "2023_Fall",
                    "STEM 100",
                    3,
                    ""
            ));

            schedule.favoriteCourse(new Course(
                    "Art Class",
                    "ART 201",
                    "B",
                    "ART",
                    professors,
                    favoriteTimes,
                    "2023_Fall",
                    "SHAL 316",
                    3,
                    ""
            ));
        } catch (CourseTimeConflictsException e) {
            throw new RuntimeException("Error building initial schedule", e);
        }

        return schedule;
    }


    public static Schedule getSchedule() {
        return schedule;
    }
}