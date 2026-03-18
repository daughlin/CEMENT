package edu.gcc.cement;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class ScheduleStorage {
    private static final String FILE_NAME = "schedule.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void saveSchedule(Schedule schedule) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), schedule);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Schedule loadSchedule() {
        try {
            File file = new File(FILE_NAME);

            if (!file.exists()) {
                return new Schedule();
            }

            return mapper.readValue(file, Schedule.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Schedule();
        }
    }
}