package edu.gcc.cement;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class ScheduleStorage {
    private static final ObjectMapper mapper = new ObjectMapper();

    // Update to accept a semester name for dynamic file paths
    public static void saveSchedule(Schedule schedule) {
        String fileName = "schedule_" + schedule.getSemester() + ".json";
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(fileName), schedule);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Schedule loadSchedule(String semester) {
        String fileName = "schedule_" + semester + ".json";
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                return new Schedule(semester); // Return new empty schedule if none exists
            }
            return mapper.readValue(file, Schedule.class);
        } catch (IOException e) {
            return new Schedule(semester);
        }
    }
}