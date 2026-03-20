package edu.gcc.cement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



public class Search {

    private String[] query;
    private ArrayList<Filter> filters;
    private ArrayList<Course> courseList;
    private ArrayList<Course> results;

    /**
     * Constructor
     * @param query
     * @param filters
     */
    public Search(String query, ArrayList<Filter> filters, ArrayList<Course> courses){
        this.query = query.split(" ");
        this.filters = filters;
        this.results = new ArrayList<Course>();
        this.courseList = new ArrayList<Course>();

        this.results.addAll(courses);
        //call update results
        updateResults();
    }

    /**
     * Constructor
     * @param query
     * @param filters
     */
    public Search(String query, ArrayList<Filter> filters) throws Exception{
        this.query = query.split(" ");
        this.filters = filters;
        this.results = new ArrayList<Course>();
        readCourses();
        if(courseList == null) {
            throw new Exception("Failed to find course list");
        }
        this.results.addAll(courseList);
        //call update results
        updateResults();
    }

    /**
     * Getters and Setters
     *
     */

    public ArrayList<Course> getResults() {
        return results;
    }

    public String getQuery() {
        String ret = "";
        for (String s : query) {
            ret = ret.concat(s);
        }
        return ret;
    }

    public ArrayList<Filter> getFilters() {
        return filters;
    }

    public void setQuery(String query) {
        this.query = query.split(" ");
        updateResults();
    }

    /**
     * Add a filter
     * @param filter
     */
    public void addFilter(Filter filter){
        this.filters.add(filter);
        updateResults();
    }

    /**
     * Remove a filter
     * @param filter
     */
    public void removeFilter(Filter filter){
        this.filters.remove(filter);
        ArrayList<Course> courses = new ArrayList<>();
        courses.addAll(this.courseList);
        this.results = courses;
        updateResults();
    }

    /**
     * Update the results
     * @return
     */
    private ArrayList<Course> updateResults(){
        ArrayList<Course> updated = new ArrayList<Course>();
        for(Course course : results) {
            //check if each course matches the query and filters
            if (matchesQuery(course) && matchesFilters(course)) {
                updated.add(course);
            }
        }
        results = updated;
        return results;
    }

    /**
     * Helper function to check if a course matches the query of the search, currently on the basis of course name, course code, course time, or professors
     * @param course
     * @return true or false depending on if it matches the query
     */
    private boolean matchesQuery(Course course) {
        if (query == null || query.length == 0) {
            return true;
        }

        String match = (course.getCourseCode() + " " + course.getName() + " " + course.getProfessors().get(0) + " " + course.getCredits() + " credits ").toLowerCase();

        for (String q : query) {
            if (!match.contains(q)) {
                return false;
            }
        }

        return true;
    }

    private boolean matchesFilters(Course course) {
        for (Filter filter : filters) {
            //System.out.println("Checking " + filter.getValue());
            switch(filter.getType()) {
                case DEPT:
                    //System.out.println(filter.getValue() + " vs " + course.getDepartment());
                    //System.out.println(!(course.getDepartment().contains(filter.getValue())));
                    if (!(course.getDepartment().equalsIgnoreCase(filter.getValue()))) {
                        return false;
                    }
                    break;
                case PROF:
                    //System.out.println("prof");
                    boolean counts = false;
                    for (String prof : course.getProfessors()) {
                        //System.out.println(prof + " vs " + filter.getValue());
                        if ((prof.toLowerCase().contains(filter.getValue().toLowerCase()))) {
                            counts =  true;
                            break;
                        }
                    }
                    if (!counts) {
                        return false;
                    }
                    break;
                case START:
                    for (Time time : course.getTimes()) {
                        //System.out.println(toMinutes(filter.getValue()));
                        if (time.getStartTime() < Integer.parseInt(filter.getValue())){
                            return false;
                        }
                    }
                    break;
                case END:
                    for (Time time : course.getTimes()) {
                        if (time.getEndTime() > Integer.parseInt(filter.getValue())){
                            return false;
                        }
                    }
                    break;
                case DAYS:
                    for (Time time : course.getTimes()) {
                        if (!filter.getValue().contains(time.getDay())){
                            return false;
                        }
                    }
                    break;
                case CREDITS:
                    //System.out.println("credits");
                    if (!(("" + course.getCredits()).equalsIgnoreCase(filter.getValue()))) {
                        return false;
                    }
                    break;
                default:
            }
        }
        return true;
    }

    private void readCourses() throws IOException {
        this.courseList = new ArrayList<Course>();
        ArrayList<Course> courses = new ArrayList<Course>();

        String classFile = "./backend/src/main/resources/data_wolfe.json";

        File f = new File(classFile);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;

        try {
            root = mapper.readTree(f);

            // building the courses from the json
            for (JsonNode c : root.get("classes")) {
                String name = c.path("name").asText();
                String dept = c.path("subject").asText();
                String number = c.path("number").asText();
                int credits = c.path("credits").asInt();
                String section = c.path("section").asText();

                ArrayList<String> professors = new ArrayList<String>();
                for(JsonNode prof : c.path("faculty")) {
                    professors.add(prof.asText(""));
                }
                ArrayList<Time> times = parseTimes(c.path("times"));
                String semester = c.path("semester").asText();
                String location = c.path("location").asText();

                courses.add(new Course(name, dept + " " + number, section, dept, professors, times, semester, location, credits, ""));

            }
            this.courseList.addAll(courses);
            System.out.println(courseList.getFirst().getCourseCode());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            courseList = null;
        }


    }

    /**
     * Helper function to parse class times by day, start time, and end time from the json file
     * @param timesNode
     * @return ArrayList of time objects for the Course class
     */
    private static ArrayList<Time> parseTimes(JsonNode timesNode) {
        ArrayList<Time> times = new ArrayList<>();
        if (timesNode == null || !timesNode.isArray()) return times;

        for (JsonNode t : timesNode) {
            String day = t.path("day").asText("");
            int start = toMinutes(t.path("start_time").asText(""));
            int end   = toMinutes(t.path("end_time").asText(""));

            if (!day.isBlank() && start >= 0 && end >= 0) {
                times.add(new Time(day, start, end));
            }
        }
        return times;
    }

    /**
     * Helper function to convert time from HH:MM:SS format to minutes from midnight
     * @param hhmmss
     * @return Time converted to minutes from midnight
     */
    private static int toMinutes(String hhmmss) {
        if (hhmmss == null || hhmmss.isBlank()) return -1;
        String[] parts = hhmmss.split(":");
        if (parts.length < 2) return -1;

        try {
            int h = Integer.parseInt(parts[0]);
            int m = Integer.parseInt(parts[1]);
            return h * 60 + m;
        } catch (NumberFormatException e) {
            return -1;
        }
    }


}
