package edu.gcc.cement;

import java.util.ArrayList;

public class ChatSearchRequest {
    private String query;
    private ArrayList<Filter> filters;

    public ChatSearchRequest() {
        this.query = "";
        this.filters = new ArrayList<>();
    }

    public ChatSearchRequest(String query, ArrayList<Filter> filters) {
        this.query = query;
        this.filters = filters;
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

    public void setFilters(ArrayList<Filter> filters) {
        this.filters = filters;
    }
}