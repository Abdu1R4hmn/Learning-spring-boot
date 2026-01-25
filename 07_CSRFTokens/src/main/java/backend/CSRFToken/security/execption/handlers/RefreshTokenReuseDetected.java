package backend.Habit_Tracker.security.execption.handlers;


import backend.Habit_Tracker.security.execption.BussinessException;

public class RefreshTokenReuseDetected extends BussinessException {

    public RefreshTokenReuseDetected() {
        super("Invalid refresh token");
    }
}
