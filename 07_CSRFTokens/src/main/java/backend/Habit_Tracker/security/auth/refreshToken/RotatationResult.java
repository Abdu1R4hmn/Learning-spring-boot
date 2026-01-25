package backend.Habit_Tracker.security.auth.refreshToken;


import backend.Habit_Tracker.entity.User;

public record RotatationResult(
        User user,
        String token
) {
}
