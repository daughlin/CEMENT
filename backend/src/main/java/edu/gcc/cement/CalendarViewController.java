package edu.gcc.cement;

import io.javalin.Javalin;

import java.util.Map;

public class CalendarViewController {

    //private static Schedule schedule = ScheduleStorage.loadSchedule();

    public static void registerRoutes(Javalin app) {

        app.get("/calendar", ctx -> {
            ctx.redirect("/pages/CalendarView.html");
        });

        app.get("/schedule", ctx -> {
            String semester = ctx.queryParam("semester");
            if (semester == null || semester.isBlank()) {
                ctx.status(400).result("Semester parameter is required");
                return;
            }
            Schedule schedule = ScheduleStorage.loadSchedule(semester);
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
                Schedule schedule = ScheduleStorage.loadSchedule(newCourse.getSemester());

                if (schedule.containsCourse(newCourse)) {
                    ctx.status(200).result("Course already exists");
                    return;
                }

                schedule.addCourse(newCourse);
                ScheduleStorage.saveSchedule(schedule);

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
                String semester = data.get("semester");

                Schedule schedule = ScheduleStorage.loadSchedule(semester);

                schedule.removeCourseByNameAndSection(name, section);
                ScheduleStorage.saveSchedule(schedule);

                ctx.result("Course removed");

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Internal Server Error");
            }
        });

        app.get("/favorites", ctx -> {
            String semester = ctx.queryParam("semester");
            if (semester == null || semester.isBlank()) {
                ctx.status(400).result("Semester parameter is required");
                return;
            }
            Schedule schedule = ScheduleStorage.loadSchedule(semester);
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
                Schedule schedule = ScheduleStorage.loadSchedule(newCourse.getSemester());

                boolean added = schedule.favoriteCourse(newCourse);

                if (!added) {
                    ctx.result("Course already favorited");
                    return;
                }

                ScheduleStorage.saveSchedule(schedule);
                ctx.result("Course favorited");

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Server Error");
            }
        });

        app.delete("/favorites", ctx -> {
            try {
                Map<String, String> data = ctx.bodyAsClass(Map.class);

                String name = data.get("name");
                String section = data.get("section");
                String semester = data.get("semester");

                Schedule schedule = ScheduleStorage.loadSchedule(semester);

                schedule.unfavoriteCourse(name, section);
                ScheduleStorage.saveSchedule(schedule);

                ctx.result("Course unfavorited");

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Server Error");
            }
        });

        app.patch("/schedule/courses/color", ctx -> {
            try {
                Map<String, String> data = ctx.bodyAsClass(Map.class);

                String name = data.get("name");
                String section = data.get("section");
                String color = data.get("color");
                String semester = data.get("semester");

                Schedule schedule = ScheduleStorage.loadSchedule(semester);

                schedule.updateCourseColor(name, section, color);
                ScheduleStorage.saveSchedule(schedule);

                ctx.result("Course color updated");

            } catch (Exception e) {
                e.printStackTrace();
                ctx.status(500).result("Internal Server Error");
            }
        });
    }

    public static Schedule getSchedule(String semester) {
        return ScheduleStorage.loadSchedule(semester);
    }
}