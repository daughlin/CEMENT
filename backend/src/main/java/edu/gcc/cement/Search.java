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
    }

    /**
     * Add a filter
     * @param filter
     */
    public void addFilter(Filter filter){
    }

    /**
     * Remove a filter
     * @param filter
     */
    public void removeFilter(Filter filter){
    }

    /**
     * Update the results
     * @return
     */
    public ArrayList<Course> updateResults(){
        ArrayList<Course> updated = new ArrayList<Course>();
        for(Course course : results) {
            //check if each course matches the query
            if (matchesQuery(course)) {
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


        //check all possible parameters that the query could match
        return course.getName().toLowerCase().contains(q)
                || course.getCourseCode().toLowerCase().contains(q)
                || course.getProfessors().contains(q)
                || q.equals("" + course.getCredits() + " credits")
                || q.equals("" + course.getCredits() + " credit");


    }


}
