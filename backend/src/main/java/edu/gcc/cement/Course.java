package edu.gcc.cement;
import java.util.ArrayList;

public class Course {

private String name;
private String courseCode;
private String section;
private String department;
private String professor;
private ArrayList<Time> times;
private String semester;
private String location;
private int credits;
private String description;



    /**Constructor
     *
     * @param courseCode
     * @param section
     * @param department
     * @param professor
     * @param times
     * @param semester
     * @param description
     */
    public Course(String name, String courseCode, String section, String department, String professor, ArrayList<Time> times, String semester, String location, int credits, String description){
        this.name = name;
        this.courseCode = courseCode;
        this.section = section;
        this.department = department;
        this.professor = professor;
        this.semester = semester;
        this.location = location;
        this.credits = credits;
        this.description = description;

        this.times = new ArrayList<Time>();
        this.times.addAll(times);

    }

    /**
     *Getters and Setters
     */
    public String getName() {return name;}
    public String getCourseCode() {return courseCode;}
    public void setCourseCode(String courseCode) {}
    public String getSection() {return section;}
    public void setSection(String section) {}
    public String getDepartment() {return department;}
    public void setDepartment(String department) {department = department;}
    public String getProfessor() {return professor;}
    public void setProfessor(String professor) {professor = professor;}

    public ArrayList<Time> getTimes() {
        return times;
    }

    public String getSemester() {return semester;}
    public void setSemester(String semester) {semester = semester;}
    public String getLocation() {return location;}
    public int getCredits() {return credits;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
}

//cole was here
