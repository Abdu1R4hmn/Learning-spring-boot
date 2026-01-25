package backend.Habit_Tracker.security.responses;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseApi(
    int status,
    String message,
    List<String> errorList,
    LocalDateTime timestamp

) {
    public ErrorResponseApi(int status, String message, List<String> errorList) {
        this(status, message, errorList, LocalDateTime.now());
    };

    public ErrorResponseApi(int status, String message) {
        this(status, message, null, LocalDateTime.now());
    };

}
