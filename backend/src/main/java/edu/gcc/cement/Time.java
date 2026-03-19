package edu.gcc.cement;

public class Time {
    private String day;
    private int startTime;
    private int endTime;

    public Time() {}

    public Time(String day, int startTime, int endTime) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getDay() {
        return day;
    }
    public int getStartTime() {
        return startTime;
    }
    public int getEndTime() {
        return endTime;
    }

    public void setDay(String day) { this.day = day; }
    public void setStartTime(int startTime) { this.startTime = startTime; }
    public void setEndTime(int endTime) { this.endTime = endTime; }
}
