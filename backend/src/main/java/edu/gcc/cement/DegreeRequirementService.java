package edu.gcc.cement;

import java.util.List;

public class DegreeRequirementService {

    public static boolean isRequired(Course course, Degree degree) {
        if (course == null || degree == null) {
            return false;
        }

        String courseCode = normalizeCourseCode(course.getCourseCode());

        for (Requirement requirement : degree.getRequirements()) {
            if (!requirement.getType().equalsIgnoreCase("required")) {
                continue;
            }

            for (String requiredCode : requirement.getCourses()) {
                if (courseCode.equals(normalizeCourseCode(requiredCode))) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isElective(Course course, Degree degree) {
        if (course == null || degree == null) {
            return false;
        }

        String courseCode = normalizeCourseCode(course.getCourseCode());

        for (Requirement requirement : degree.getRequirements()) {
            if (!requirement.getType().equalsIgnoreCase("elective")) {
                continue;
            }

            for (String electiveCode : requirement.getCourses()) {
                if (courseCode.equals(normalizeCourseCode(electiveCode))) {
                    return true;
                }
            }
        }

        return false;
    }

    public static String getRequirementCategory(Course course, Degree degree) {
        if (course == null || degree == null) {
            return null;
        }

        String courseCode = normalizeCourseCode(course.getCourseCode());

        for (Requirement requirement : degree.getRequirements()) {
            for (String code : requirement.getCourses()) {
                if (courseCode.equals(normalizeCourseCode(code))) {
                    return requirement.getCategory();
                }
            }
        }

        return null;
    }

    private static String normalizeCourseCode(String code) {
        if (code == null) {
            return "";
        }

        return code.trim()
                .toUpperCase()
                .replaceAll("\\s+", " ");
    }
}