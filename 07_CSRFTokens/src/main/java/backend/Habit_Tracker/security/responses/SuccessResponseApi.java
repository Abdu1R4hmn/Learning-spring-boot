package backend.Habit_Tracker.security.responses;

import java.time.LocalDateTime;

public record SuccessResponseApi<T>(
        int status,
        String message,
        T data,
        LocalDateTime timestamp


) {
    public SuccessResponseApi(int status, String message, T data, LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = timestamp;
    }
}
