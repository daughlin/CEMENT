package edu.gcc.cement;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

public class DegreeLoader {

    public static Degree loadDegree(String degreeName) throws Exception {
        String fileName = degreeName + ".json";

        // 👇 CHANGE THIS LINE
        String path = "/degree-jsons/" + fileName;

        InputStream inputStream = DegreeLoader.class.getResourceAsStream(path);

        if (inputStream == null) {
            throw new Exception("Degree file not found: " + path);
        }

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputStream, Degree.class);
    }
}