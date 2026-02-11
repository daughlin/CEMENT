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
        return null;
    }
}
