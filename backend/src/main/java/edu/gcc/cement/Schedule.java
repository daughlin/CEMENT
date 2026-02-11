package edu.gcc.cement;

import java.util.ArrayList;

public class Schedule {

    private ArrayList<Course> courses;
    private String semester;

    /**
     * Constructor
     * @param semester
     */
    public Schedule(String semester){
        this.semester = semester;
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
     * @param courseID
     */
    public void addCourse(String courseID){
    }

    /**
     * remove a course
     * @param courseID
     */
    public void removeCourse(String courseID){
    }


}
