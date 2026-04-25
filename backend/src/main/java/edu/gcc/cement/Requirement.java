package edu.gcc.cement;

import java.util.ArrayList;

public class Requirement {
    private String type;
    private String category;
    private int creditsRequired;
    private ArrayList<String> courses;

    public Requirement() {
        courses = new ArrayList<>();
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public int getCreditsRequired() {
        return creditsRequired;
    }

    public ArrayList<String> getCourses() {
        return courses;
    }
}