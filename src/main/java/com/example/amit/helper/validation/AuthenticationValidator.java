package com.example.amit.helper.validation;


import com.example.amit.exception.AccessDeniedException;
import com.example.amit.models.User;
import com.example.amit.repository.UserRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Optional;



@Component
@NullMarked
public class AuthenticationValidator {

//    public void validateLoginRequest(LoginRequest request) {
//        Optional.of(request).filter(req -> StringUtils.hasText(req.username()) && StringUtils.hasText(req.password()))
//                .orElseThrow(() -> new UnauthorizedRequestException("Invalid login request: username and password are required."));
//    }


    public void validateUserLockState(User user, Instant now) {
        Optional.of(user)
                .map(User::getLockedUntil)
                .filter(lockTime -> lockTime.isAfter(now))
                .ifPresent(lockTime -> {
                    throw new AccessDeniedException("Account locked until %s".formatted(lockTime));
                });
    }

    public User unlockIfExpired(User e, Instant now, UserRepository r) {
        if (e.getLockedUntil() != null && e.getLockedUntil().isBefore(now)) {
            e.setAttempts(0);
            e.setLockedUntil(null);
            return r.save(e);
        }
        return e;
    }

//    public void validateForgotPasswordRequest(ForgotPasswordRequest request) {
//        Optional.of(request).filter(ForgotPasswordRequest::isValidRequest)
//                .orElseThrow(() -> new BadRequestException("Invalid request: Passwords do not match."));
//    }
}
