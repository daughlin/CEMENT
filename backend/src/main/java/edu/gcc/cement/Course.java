package edu.gcc.cement;

public class Course {

private String courseCode;
private String section;
private String department;
private String professor;
private String day;
private int startTime;
private int endTime;
private String semester;
private String description;

    /**Constructor
     *
     * @param courseCode
     * @param section
     * @param department
     * @param professor
     * @param day
     * @param startTime
     * @param endTime
     * @param semester
     * @param description
     */
    public Course(String courseCode, String section, String department, String professor, String day, int startTime, int endTime, String semester, String description){
        this.courseCode = courseCode;
        this.section = section;
        this.department = department;
        this.professor = professor;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.semester = semester;
        this.description = description;

    }

    /**
     *Getters and Setters
     */
    public String getCourseCode() {return courseCode;}
    public void setCourseCode(String courseCode) {}
    public String getSection() {return section;}
    public void setSection(String section) {}
    public String getDepartment() {return department;}
    public void setDepartment(String department) {department = department;}
    public String getProfessor() {return professor;}
    public void setProfessor(String professor) {professor = professor;}
    public String getDay() {return day;}
    public void setDay(String day) {this.day = day;}
    public int getStartTime() {return startTime;}
    public void setStartTime(int startTime) {this.startTime = startTime;}
    public int getEndTime() {return endTime;}
    public void setEndTime(int endTime) {this.endTime = endTime;}
    public String getSemester() {return semester;}
    public void setSemester(String semester) {semester = semester;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
}
