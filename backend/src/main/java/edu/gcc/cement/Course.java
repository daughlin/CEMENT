package edu.gcc.cement;
import java.util.ArrayList;

public class Course {

private String name;
private String courseCode;
private String section;
private String department;
private ArrayList<String> professors;
private ArrayList<Time> times;
private String semester;
private String location;
private int credits;
private String description;
private String displayColor;


    public Course() {
        professors = new ArrayList<String>();
        times = new ArrayList<Time>();
        displayColor = "#7A958F";
    }
    /**Constructor
     * @param name
     * @param courseCode
     * @param section
     * @param department
     * @param professors
     * @param times
     * @param semester
     * @param location
     * @param credits
     * @param description
     */
    public Course(String name, String courseCode, String section, String department, ArrayList<String> professors, ArrayList<Time> times, String semester, String location, int credits, String description){
        this.name = name;
        this.courseCode = courseCode;
        this.section = section;
        this.department = department;
        this.professors = professors;
        this.semester = semester;
        this.location = location;
        this.credits = credits;
        this.description = description;

        this.times = new ArrayList<Time>();
        this.times.addAll(times);

        displayColor = "#7A958F";

    }

    /**
     *Getters and Setters
     */
    public String getName() {return name;}
    public void setName(String name) {this.name = name;}
    public String getCourseCode() {return courseCode;}
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
    public String getSection() {return section;}
    public void setSection(String section) {this.section = section;}
    public String getDepartment() {return department;}
    public void setDepartment(String department) {this.department = department;}
    public ArrayList<String> getProfessors() {return professors;}
    public void setProfessor(ArrayList<String> professors) {this.professors = professors;}

    public ArrayList<Time> getTimes() {
        return times;
    }
    public void setTimes(ArrayList<Time> times) {
        this.times = times;
    }

    public String getSemester() {return semester;}
    public void setSemester(String semester) {this.semester = semester;}
    public String getLocation() {return location;}
    public void setLocation(String location) {this.location = location;}
    public int getCredits() {return credits;}
    public void setCredits(int credits) {this.credits = credits;}

    public String getDisplayColor() { return displayColor; }
    public void setDisplayColor(String color) { this.displayColor = color; }
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
    public String daysHelper() {
        String ret = "";
        for (Time time : this.times) {
            ret = ret.concat(time.getDay());
        }
        return ret;
    }
    public String niceTimeHelper() {
        if (times.size() == 0) {
            return "Irregular meeting times";
        }
        return to12HourTime(times.getFirst().getStartTime()) + " - " + to12HourTime(times.getFirst().getEndTime());
    }

    public static String to12HourTime(int minutesPastMidnight) {
        if (minutesPastMidnight < 0 || minutesPastMidnight >= 24 * 60) {
            throw new IllegalArgumentException("Minutes must be between 0 and 1439.");
        }

        int hour24 = minutesPastMidnight / 60;
        int minute = minutesPastMidnight % 60;

        String amPm = (hour24 < 12) ? "AM" : "PM";

        int hour12;
        if (hour24 == 0) {
            hour12 = 12; // midnight
        } else if (hour24 > 12) {
            hour12 = hour24 - 12;
        } else {
            hour12 = hour24;
        }

        return String.format("%d:%02d %s", hour12, minute, amPm);
    }
}

//cole was here
