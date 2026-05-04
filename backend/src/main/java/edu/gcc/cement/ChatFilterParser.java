package edu.gcc.cement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFilterParser {

    private final Set<String> validDepartments;
    private final Set<String> validProfessors;

    public ChatFilterParser(ArrayList<Course> courses) {
        validDepartments = new HashSet<>();
        validProfessors = new HashSet<>();

        for (Course course : courses) {
            if (course.getDepartment() != null) {
                validDepartments.add(course.getDepartment());
            }
            if (course.getProfessors() != null) {
                validProfessors.addAll(course.getProfessors());
            }
        }
    }

    public ChatSearchRequest parse(String message) {
        ArrayList<Filter> filters = new ArrayList<>();

        if (message == null || message.isBlank()) {
            return new ChatSearchRequest("", filters);
        }

        String lower = message.toLowerCase();
        String leftover = lower;

        leftover = parseCredits(leftover, filters);
        leftover = parseDays(leftover, filters);
        leftover = parseTimes(leftover, filters);
        leftover = parseDepartment(leftover, filters);
        leftover = parseProfessor(leftover, filters);
        leftover = parseMajorRequirement(leftover, filters);
        leftover = parseSpecialPhrases(leftover, filters);

        leftover = cleanupQuery(leftover);

        return new ChatSearchRequest(leftover, filters);
    }

    private String parseCredits(String message, ArrayList<Filter> filters) {
        Matcher matcher = Pattern.compile("(\\d+)\\s*credit").matcher(message);
        if (matcher.find()) {
            filters.add(new Filter(matcher.group(1), Type.CREDITS));
            return matcher.replaceAll(" ");
        }
        return message;
    }

    private String parseDays(String message, ArrayList<Filter> filters) {
        if (message.contains("mwf")) {
            filters.add(new Filter("MWF", Type.DAYS));
            message = message.replace("mwf", " ");
        } else if (message.contains("tr") || message.contains("tth") || message.contains("t/r")) {
            filters.add(new Filter("TR", Type.DAYS));
            message = message.replace("tth", " ").replace("t/r", " ").replace("tr", " ");
        } else {
            StringBuilder days = new StringBuilder();
            if (message.contains("monday")) days.append("M");
            if (message.contains("tuesday")) days.append("T");
            if (message.contains("wednesday")) days.append("W");
            if (message.contains("thursday")) days.append("R");
            if (message.contains("friday")) days.append("F");

            if (!days.isEmpty()) {
                filters.add(new Filter(days.toString(), Type.DAYS));
            }
        }

        return message
                .replace("monday", " ")
                .replace("tuesday", " ")
                .replace("wednesday", " ")
                .replace("thursday", " ")
                .replace("friday", " ");
    }

    private String parseTimes(String message, ArrayList<Filter> filters) {
        Matcher afterMatcher = Pattern.compile("after\\s+(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?").matcher(message);
        if (afterMatcher.find()) {
            String time = normalizeTime(afterMatcher.group(1), afterMatcher.group(2), afterMatcher.group(3));
            if (time != null) {
                filters.add(new Filter(time, Type.START));
            }
            message = afterMatcher.replaceAll(" ");
        }

        Matcher beforeMatcher = Pattern.compile("before\\s+(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?").matcher(message);
        if (beforeMatcher.find()) {
            String time = normalizeTime(beforeMatcher.group(1), beforeMatcher.group(2), beforeMatcher.group(3));
            if (time != null) {
                filters.add(new Filter(time, Type.END));
            }
            message = beforeMatcher.replaceAll(" ");
        }

        if (message.contains("morning")) {
            filters.add(new Filter("12:00", Type.END));
            message = message.replace("morning", " ");
        }

        if (message.contains("afternoon")) {
            filters.add(new Filter("12:00", Type.START));
            message = message.replace("afternoon", " ");
        }

        if (message.contains("night")) {
            filters.add(new Filter("17:00", Type.START));
            message = message.replace("night", " ");
        }

        if (message.contains("before noon")) {
            filters.add(new Filter("12:00", Type.END));
            message = message.replace("before noon", " ");
        }

        return message;
    }

    private String parseDepartment(String message, ArrayList<Filter> filters) {
        for (String dept : validDepartments) {
            if (message.contains(dept.toLowerCase())) {
                filters.add(new Filter(dept, Type.DEPT));
                return message.replace(dept.toLowerCase(), " ");
            }
        }

//        if (message.contains("computer science") && validDepartments.contains("COMP")) {
//            filters.add(new Filter("COMP", Type.DEPT));
//            return message.replace("computer science", " ");
//        }
//        if (message.contains("biology") && validDepartments.contains("BIOL")) {
//            filters.add(new Filter("BIOL", Type.DEPT));
//            return message.replace("biology", " ");
//        }
//        if (message.contains("chemistry") && validDepartments.contains("CHEM")) {
//            filters.add(new Filter("CHEM", Type.DEPT));
//            return message.replace("chemistry", " ");
//        }
//        if (message.contains("math") && validDepartments.contains("MATH")) {
//            filters.add(new Filter("MATH", Type.DEPT));
//            return message.replace("math", " ");
//        }

        return message;
    }

    private String parseProfessor(String message, ArrayList<Filter> filters) {
        for (String professor : validProfessors) {
            if (professor == null || professor.isBlank()) {
                continue;
            }

            String full = professor.toLowerCase().trim();
            if (full.isBlank()) {
                continue;
            }

            if (message.contains(full)) {
                filters.add(new Filter(professor, Type.PROF));
                return message.replace(full, " ");
            }

            String[] parts = professor.split(",");
            if (parts.length == 0) {
                continue;
            }

            String last = parts[0].trim().toLowerCase();
            if (last.isBlank()) {
                continue;
            }

            if (message.contains(last)) {
                filters.add(new Filter(professor, Type.PROF));
                return message.replace(last, " ");
            }
        }

        return message;
    }

    private String parseMajorRequirement(String message, ArrayList<Filter> filters) {
        if (message.contains("required")
                || message.contains("requirement")
                || message.contains("requirements")
                || message.contains("required for my major")
                || message.contains("in major")) {

            filters.add(new Filter("", Type.REQ));

            return message
                    .replace("required for my major", " ")
                    .replace("requirements", " ")
                    .replace("requirement", " ")
                    .replace("required", " ")
                    .replace("in major", " ");
        }

        if (message.contains("elective")
                || message.contains("electives")
                || message.contains("major elective")
                || message.contains("elective for my major")) {

            filters.add(new Filter("", Type.ELECTIVE));

            return message
                    .replace("elective for my major", " ")
                    .replace("major elective", " ")
                    .replace("electives", " ")
                    .replace("elective", " ");
        }

        return message;
    }

    private String parseSpecialPhrases(String message, ArrayList<Filter> filters) {
        return message
                .replace("find", " ")
                .replace("show me", " ")
                .replace("search for", " ")
                .replace("classes", " ")
                .replace("courses", " ")
                .replace("with", " ")
                .replace("that are", " ")
                .replace("that meet", " ")
                .replace("taught by", " ")
                .replace("dr", " ")
                .replace("dr.", " ")
                .replace("doctor", " ")
                .replace("prof", " ")
                .replace("prof.", " ")
                .replace("professor", " ")
                .replace("please", " ")
                .replace("suggest", " ")
                .replace("professor", " ")
                .replace("that", " ")
                .replace("is", " ")
                .replace("fit", " ")
                .replace("professor", " ")
                .replace("my ", " ")
                .replace("schedule", " ")
                .replace("my schedule", " ")
                .replace("can", " ")
                .replace("you", " ")
                .replace("for my major", " ")
                .replace("my major", " ")
                .replace("major", " ")
                .replace("for", " ")
                .replace("me", " ")
                .replace("that is", " ");

    }

    private String cleanupQuery(String message) {
        return message.trim().replaceAll("\\s+", " ");
    }

    private String normalizeTime(String hourStr, String minuteStr, String ampm) {
        try {
            int hour = Integer.parseInt(hourStr);
            int minute = minuteStr == null ? 0 : Integer.parseInt(minuteStr);

            if (ampm != null) {
                if (ampm.equals("pm") && hour != 12) {
                    hour += 12;
                } else if (ampm.equals("am") && hour == 12) {
                    hour = 0;
                }
            } else {
                if (hour <= 7) {
                    hour += 12;
                }
            }

            return String.format("%02d:%02d", hour, minute);
        } catch (Exception e) {
            return null;
        }
    }
}