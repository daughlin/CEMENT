package edu.gcc.cement;

import java.util.ArrayList;

public class Schedule {

    private ArrayList<Course> courses;
    private String semester;

    public Schedule() {
        this.courses = new ArrayList<>();
    }

    /**
     * Constructor
     * @param semester
     */
    public Schedule(String semester){
        this.semester = semester;
    }

    public Schedule(String semester, ArrayList<Course> courses) {
        this.semester = semester;
        this.courses = new ArrayList<Course>();
        this.courses.addAll(courses);
    }

    /**
     *Getters and Setters
     */
    public ArrayList<Course> getCourses() {
        return courses;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    /**
     * add a course
     * @param course
     */
    public void addCourse(Course course){
        this.courses.add(course);
    }

    /**
     * remove a course
     * @param courseID
     */
    public void removeCourse(String courseID){
    }


}
