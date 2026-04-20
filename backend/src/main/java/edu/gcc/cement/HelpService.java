package edu.gcc.cement;

public class HelpService {

    public String getHelpResponse(String message) {
        String lower = message.toLowerCase();

        if (lower.contains("save")) {
            return "To save a schedule, create or edit your schedule and then use the save option in the schedule view.";
        }

        if (lower.contains("filter")) {
            return "You can use filters to narrow courses by department, professor, credits, days, and start/end times.";
        }

        if (lower.contains("calendar")) {
            return "The calendar view shows your selected courses arranged by day and time so you can check for conflicts and gaps.";
        }

        return "I can help explain filters, searching, saving schedules, and the calendar view.";
    }
}