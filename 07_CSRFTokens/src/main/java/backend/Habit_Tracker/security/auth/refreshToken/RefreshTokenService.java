package backend.Habit_Tracker.security.auth.refreshToken;

import backend.Habit_Tracker.entity.User;
import backend.Habit_Tracker.security.execption.handlers.RefreshTokenExpired;
import backend.Habit_Tracker.security.execption.handlers.RefreshTokenReuseDetected;
import backend.Habit_Tracker.security.execption.handlers.ResourseNotFound;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    //    @Value("${REFRESH_TOKEN_VALIDITY_DAYS}")
    private final long refreshTokenValidity = 7;


    public String createRefreshToken(User user){

        String rawToken = generateRawToken();
        String hashedToken = hashToken(rawToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setTokenHash(hashedToken);
        refreshToken.setExpiresAt(
                Instant.now().plusSeconds(refreshTokenValidity * 24 * 60 * 60)
        );
        refreshTokenRepository.save(refreshToken);
        log.info("RefreshToken created: {}", refreshToken);
        return rawToken;
    }

    public RotatationResult validateRefreshToken(String rawToken) throws ResourseNotFound, RefreshTokenExpired, RefreshTokenReuseDetected {

        String hashToken = hashToken(rawToken);

        RefreshToken refreshToken = refreshTokenRepository.findByTokenHash(hashToken)
                .orElseThrow(() -> new ResourseNotFound("Refresh Token "));

        if (refreshToken.isRevoked() || refreshToken.getLastUsedAt() != null){
            revokeAllUserTokens(refreshToken.getUser().getId());
            log.info("Refresh Token has been revoked");
            throw new RefreshTokenReuseDetected();
        }
        log.info("Refresh Token has been validated");
        if(refreshToken.getExpiresAt().isBefore(Instant.now())){
            throw new RefreshTokenExpired();
        }
        refreshToken.setRevoked(true);
        log.info("Refresh Token has been revoked");
        refreshToken.setLastUsedAt(Instant.now());



        refreshTokenRepository.save(refreshToken);

        log.info("calling new create refresh toekn");
        String newRefreshToken = createRefreshToken(refreshToken.getUser());

        return new RotatationResult(refreshToken.getUser(), newRefreshToken);
    }


    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    public void revokeByRawToken(String rawToken) {
        String hash = hashToken(rawToken);

        refreshTokenRepository.findByTokenHash(hash)
                .ifPresent(token -> revokeAllUserTokens(token.getUser().getId()));
    }

    private String generateRawToken(){
        return UUID.randomUUID().toString();
    }

    private String hashToken(String token){
        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return byteToHex(hash);

        }catch (Exception ex){
            throw new IllegalStateException("Could not hash refresh token",ex);
        }
    }

    private String byteToHex(byte[] bytes){
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes){
            hex.append(String.format("%02x", b));
        }
        return hex.toString();
    }

}