package edu.gcc.cement;

import java.util.ArrayList;

public class Degree {
    private String degree;
    private ArrayList<Requirement> requirements;

    public Degree() {
        requirements = new ArrayList<>();
    }

    public String getDegree() {
        return degree;
    }

    public ArrayList<Requirement> getRequirements() {
        return requirements;
    }
}