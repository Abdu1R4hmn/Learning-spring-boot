package backend.Habit_Tracker.security.securityDto;

import lombok.Data;

@Data
public class LoginResponseDto {
    private final String accessToken;

    public LoginResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }
}
