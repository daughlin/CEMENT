package edu.gcc.cement;

import io.javalin.Javalin;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;

public class StudentController {

    public static void registerRoutes(Javalin app) {

        app.get("/api/majors", ctx -> {
            ArrayList<String> majors = new ArrayList<>();

            URL folderUrl = StudentController.class.getResource("/degree-pdfs");

            if (folderUrl == null) {
                ctx.status(500).result("Could not find degree_pdfs folder");
                return;
            }

            try {
                Path folderPath = Paths.get(folderUrl.toURI());

                try (Stream<Path> paths = Files.list(folderPath)) {
                    paths
                            .filter(path -> path.toString().toLowerCase().endsWith(".pdf"))
                            .forEach(path -> {
                                String fileName = path.getFileName().toString();
                                String majorName = fileName.replaceFirst("(?i)\\.pdf$", "");
                                majors.add(majorName);
                            });
                }

                Collections.sort(majors);
                ctx.json(majors);

            } catch (Exception e) {
                ctx.status(500).result("Error loading majors: " + e.getMessage());
            }
        });

        app.get("/degrees/{file}", ctx -> {
            String fileName = ctx.pathParam("file");
            InputStream fileStream = Main.class.getResourceAsStream("/degree_pdfs/" + fileName);

            if (fileStream == null) {
                ctx.status(404).result("Degree PDF not found");
                return;
            }

            ctx.contentType("application/pdf");
            ctx.result(fileStream);
        });
    }
}