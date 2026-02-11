package edu.gcc.cement;

public class Filter {
    private String value;
    private String type;

    /**
     * Constructor
     * @param value
     * @param type
     */
    public Filter(String value, String type){
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

    public String getType() {
        return type;
    }
}