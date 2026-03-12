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
    public Search(String query, ArrayList<Filter> filters){
        this.query = query;
        this.filters = filters;
        this.results = new ArrayList<>();
    }

    /**
     * Getters and Setters
     *
     */

    public ArrayList<Course> getResults() {
        ArrayList<String> profs = new ArrayList<>();
        profs.add("Dr. Brown");
        ArrayList<Time> time = new ArrayList<>();
        time.add(new Time("MWF", 9,10));
        results.add(new Course("Algebra", "MATH 101","A", "Math", profs, time, "F25","HAL 114", 3, "Learn algebra"));
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
        return null;
    }
}
