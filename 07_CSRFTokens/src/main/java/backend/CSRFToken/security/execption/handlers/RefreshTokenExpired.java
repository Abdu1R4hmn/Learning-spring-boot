package backend.Habit_Tracker.security.execption.handlers;


import backend.Habit_Tracker.security.execption.BussinessException;

public class RefreshTokenExpired extends BussinessException {
    public RefreshTokenExpired() {
        super("Invalid Refresh Token!");
    }
}
