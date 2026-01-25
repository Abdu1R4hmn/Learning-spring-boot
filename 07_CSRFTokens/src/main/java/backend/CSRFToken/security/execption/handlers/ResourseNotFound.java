package backend.Habit_Tracker.security.execption.handlers;

import backend.Habit_Tracker.security.execption.BussinessException;

public class ResourseNotFound extends BussinessException {

    public ResourseNotFound(String message) {
        super(message + " Not Found!");
    }
}
