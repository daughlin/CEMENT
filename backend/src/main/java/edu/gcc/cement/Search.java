package edu.gcc.cement;

import java.util.ArrayList;



public class Search {

    private String query;
    private ArrayList<Filter> filters;
    private ArrayList<Course> results;

    /**
     * Constructor
     * @param query
     * @param filters
     */
    public Search(String query, ArrayList<Filter> filters, ArrayList<Course> courses){
        this.query = query;
        this.filters = filters;
        this.results = new ArrayList<Course>();
        this.results.addAll(courses);
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
        return query;
    }

    public ArrayList<Filter> getFilters() {
        return filters;
    }

    public void setQuery(String query) {
        this.query = query;
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
     * Helper function to check if a course matches the query of the search, currently on the basis of course name, course code, or professors
     * @param course
     * @return true or false depending on if it matches the query
     */
    private boolean matchesQuery(Course course) {
        if (query == null || query.isBlank()) {
            return true;
        }

        String q = query.toLowerCase();

        for (String prof : course.getProfessors()) {
            if (prof.toLowerCase().contains(q)) {
                return true;
            }
        }
        //check all possible parameters that the query could match
        return course.getName().toLowerCase().contains(q)
                || course.getCourseCode().toLowerCase().contains(q)
                || q.equals("" + course.getCredits() + " credits")
                || q.equals("" + course.getCredits() + " credit");


    }

    private boolean matchesFilters(Course course) {
        for (Filter filter : filters) {
            //System.out.println("Checking " + filter.getValue());
            switch(filter.getType()) {
                case DEPT:
                    //System.out.println(filter.getValue() + " vs " + course.getDepartment());
                    //System.out.println(!(course.getDepartment().contains(filter.getValue())));
                    if (!(course.getDepartment().contains(filter.getValue()))) {
                        return false;
                    }
                    break;
                case PROF:
                    //System.out.println("prof");
                    boolean counts = false;
                    for (String prof : course.getProfessors()) {
                        if ((prof.equals(filter.getValue()))) {
                            counts =  true;
                        }
                    }
                    if (!counts) {
                        return false;
                    }
                    break;
                case TIME:
                    //waiting on max's time comparison functionality
//                    for (Time time : course.getTimes()) {
//
//                    }
                case DAYS:
                    //come back to this
                case CREDITS:
                    //System.out.println("credits");
                    if (!(("" + course.getCredits()).equals(filter.getValue()))) {
                        return false;
                    }
                    break;
                default:
            }
        }
        return true;
    }


}
