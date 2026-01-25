package backend.Habit_Tracker.security.execption.handlers;

import backend.Habit_Tracker.security.execption.BussinessException;

public class ResourseAlreadyExists extends BussinessException{

    public ResourseAlreadyExists(String message){
        super(message + " Already Exits!!");
    }
}
