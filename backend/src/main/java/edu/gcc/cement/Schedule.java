package edu.gcc.cement;

import java.util.ArrayList;

import java.util.Iterator;



public class Schedule {

    private ArrayList<Course> courses;
    private String semester;

    private ArrayList<Course> favorites;

    public Schedule() {
        this.courses = new ArrayList<>();
        this.favorites = new ArrayList<>();
    }

    /**
     * Constructor
     * @param semester
     */
    public Schedule(String semester){
        this.semester = semester;
        this.courses = new ArrayList<>();
        this.favorites = new ArrayList<>();
    }

    public Schedule(String semester, ArrayList<Course> courses) {
        this.semester = semester;
        this.favorites = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.courses.addAll(courses);
    }

    /**
     *Getters and Setters
     */
    public ArrayList<Course> getCourses() {
        return courses;
    }

    public ArrayList<Course> getFavorites(){return favorites;}


    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    /**
     * add a course
     * @param c
     */
    public void addCourse(Course c) throws CourseTimeConflictsException {
        for (Course existingCourse : courses) {
            for (Time existingTime : existingCourse.getTimes()) {
                for (Time newTime : c.getTimes()) {
                    if (timesOverlap(existingTime, newTime)) {
                        throw new CourseTimeConflictsException(
                                buildConflictMessage(c, newTime, existingCourse, existingTime)
                        );
                    }
                }
            }
        }
        courses.add(c);
    }

    /**
     * remove a course
     * @param c
     */
    public void removeCourse(Course c){
        courses.remove(c);
    }



    public void removeCourseByNameAndSection(String name, String section) {
        Iterator<Course> iterator = courses.iterator();

        while (iterator.hasNext()) {
            Course c = iterator.next();

            if (c.getName().equals(name) && c.getSection().equals(section)) {
                iterator.remove();
            }
        }
    }


    private boolean timesOverlap(Time t1, Time t2) {
        if (!t1.getDay().equalsIgnoreCase(t2.getDay())) {
            return false;
        }

        return t1.getStartTime() < t2.getEndTime()
                && t1.getEndTime() > t2.getStartTime();
    }

    private String buildConflictMessage(Course newCourse, Time newTime,
                                        Course existingCourse, Time existingTime) {
        return "Course " + newCourse.getCourseCode()
                + " conflicts with " + existingCourse.getCourseCode()
                + " on " + newTime.getDay()
                + " from " + formatTimeRange(newTime)
                + ". Existing course meets from " + formatTimeRange(existingTime) + ".";
    }

    private String formatTimeRange(Time t) {
        return formatMinutes(t.getStartTime()) + " - " + formatMinutes(t.getEndTime());
    }

    private String formatMinutes(int minutesFromMidnight) {
        int hour24 = minutesFromMidnight / 60;
        int minutes = minutesFromMidnight % 60;

        String period = (hour24 < 12) ? "AM" : "PM";

        int hour12 = hour24 % 12;
        if (hour12 == 0) {
            hour12 = 12;
        }

        return String.format("%d:%02d %s", hour12, minutes, period);
    }

    public boolean containsCourse(Course course) {
        Iterator<Course> iterator = courses.iterator();

        while (iterator.hasNext()) {
            Course c = iterator.next();

            if (c.getName().equals(course.getName()) &&
                    c.getSection().equals(course.getSection())) {
                return true;
            }
        }

        return false;
    }

    public boolean favoriteCourse(Course course) {
        Iterator<Course> iterator = favorites.iterator();

        while (iterator.hasNext()) {
            Course c = iterator.next();

            if (c.getName().equals(course.getName()) &&
                    c.getSection().equals(course.getSection())) {
                return false;
            }
        }

        favorites.add(course);
        return true;
    }

    public void unfavoriteCourse(String name, String section) {
        Iterator<Course> iterator = favorites.iterator();

        while (iterator.hasNext()) {
            Course c = iterator.next();

            if (c.getName().equals(name) &&
                    c.getSection().equals(section)) {
                iterator.remove();
            }
        }
    }

    public boolean isFavorite(Course course) {
        for (Course c : favorites) {
            if (c.getName().equals(course.getName()) &&
                    c.getSection().equals(course.getSection())) {
                return true;
            }
        }
        return false;
    }


    public boolean canAddCourse(Course c) {
        for (Course existingCourse : courses) {
            for (Time existingTime : existingCourse.getTimes()) {
                for (Time newTime : c.getTimes()) {
                    if (timesOverlap(existingTime, newTime)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public void updateCourseColor(String name, String section, String color) {
        Iterator<Course> iterator = courses.iterator();

        while (iterator.hasNext()) {
            Course c = iterator.next();

            if (c.getName().equals(name) && c.getSection().equals(section)) {
                c.setDisplayColor(color);
                return;
            }
        }
    }

}
