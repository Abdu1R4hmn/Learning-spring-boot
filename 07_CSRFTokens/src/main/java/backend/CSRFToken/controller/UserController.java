package backend.Habit_Tracker.controller;

import backend.Habit_Tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/to")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }


    @GetMapping("/user")
    public String userView(){
        return userService.UserView();
    }

    @GetMapping("/admin")
    public String adminView(){
        return userService.AdminView();
    }
}
