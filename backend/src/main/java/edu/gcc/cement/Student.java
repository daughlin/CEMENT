package edu.gcc.cement;

public class Student {
    private String name;
    private String major;
    private Schedule schedule;
    private Search search;

    /**
     * Constructor
     * @param name
     */
    public Student(String name){
        this.name = name;
        this.schedule = new Schedule("Fall");
    }

    /**
     * Getters and Setters
     */
    public String getMajor() { return major; }
    public void setMajor(String major) {this.major = major;}
    public Schedule getSchedule() { return schedule; }

    public Search getSearch() { return search; }
    public void setSearch(Search search) { this.search = search; }

    /**
     * Student searching for a course with a query and/or filter
     * @param query
     * @param filter
     */
    public void search(String query, Filter filter){}


}
