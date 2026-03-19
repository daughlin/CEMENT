package edu.gcc.cement;

enum Type {
    DEPT, PROF, START, END, DAYS, CREDITS;
}

public class Filter {
    private String value;
    private Type type;

    /**
     * Constructor
     * @param value
     * @param type
     */
    public Filter(String value, Type type){
        this.value = value;
        this.type = type;
    }

    /**
     * Getters and Setters
     */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Type getType() {
        return type;
    }
}