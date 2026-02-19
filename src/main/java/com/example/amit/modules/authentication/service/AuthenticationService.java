package com.example.amit.modules.authentication.service;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.example.amit.modules.authentication.dto.response.AuthenticationResponse;
import com.example.amit.modules.authentication.dto.request.VerificationRequest;
import com.example.amit.modules.authentication.dto.request.RegisterRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AuthenticationManager;
import com.example.amit.modules.authentication.dto.request.LoginRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import com.example.amit.security.service.UserPrincipal;
import com.example.amit.security.service.JwtService;
import jakarta.persistence.EntityNotFoundException;
import com.example.amit.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import com.example.amit.common.constants.Role;
import org.springframework.http.HttpHeaders;
import lombok.RequiredArgsConstructor;
import com.example.amit.models.User;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import java.time.Duration;
import java.util.Set;




@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TwoFactorAuthenticationService tfaService;

    private static final String COOKIE_NAME = "accessToken";
    private static final long COOKIE_MAX_AGE = 60 * 60; // 1 hour


    public AuthenticationResponse register(RegisterRequest request, HttpServletResponse response) {

        var user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.SYSTEM_ADMIN)
                .mfaEnabled(request.mfaEnabled())
                .build();

        if (request.mfaEnabled()) {
            user.setSecret(tfaService.generateNewSecret());
        }

        var savedUser = repository.save(user);

        if (!savedUser.isMfaEnabled()) {
            UserPrincipal principal = buildPrincipal(savedUser);
            String jwtToken = jwtService.generateToken(principal);
            setAccessTokenCookie(response, jwtToken);
        }

        return AuthenticationResponse.builder()
                .mfaEnabled(savedUser.isMfaEnabled())
                .secretImageUri(
                        savedUser.isMfaEnabled()
                                ? tfaService.generateQrCodeImageUri(savedUser.getSecret())
                                : null
                )
                .build();
    }


    public AuthenticationResponse login(LoginRequest request, HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        log.info("User authenticated: {}", authentication.isAuthenticated());

        var user = repository.findUserByEmail(request.email())
                .orElseThrow();

        if (user.isMfaEnabled()) {
            return AuthenticationResponse.builder()
                    .mfaEnabled(true)
                    .build();
        }

        UserPrincipal principal = buildPrincipal(user);
        String jwtToken = jwtService.generateToken(principal);

        setAccessTokenCookie(response, jwtToken);

        return AuthenticationResponse.builder()
                .mfaEnabled(false)
                .build();
    }


    public AuthenticationResponse verifyCode(VerificationRequest request,
                                             HttpServletResponse response) {

        var user = repository.findUserByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException(
                        "No user found with " + request.email()
                ));

        if (tfaService.isOtpNotValid(user.getSecret(), request.code())) {
            throw new BadCredentialsException("Invalid 2FA code");
        }

        UserPrincipal principal = buildPrincipal(user);
        String jwtToken = jwtService.generateToken(principal);

        setAccessTokenCookie(response, jwtToken);

        return AuthenticationResponse.builder()
                .mfaEnabled(true)
                .build();
    }


    public void logout(HttpServletResponse response) {
        clearAccessTokenCookie(response);
    }


    private void setAccessTokenCookie(HttpServletResponse response,
                                      String token) {

        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(false) // ⚠ true in production (https)
                .path("/")
                .maxAge(Duration.ofSeconds(COOKIE_MAX_AGE))
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearAccessTokenCookie(HttpServletResponse response) {

        ResponseCookie cookie = ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


    private UserPrincipal buildPrincipal(User user) {

        Set<Role> roles = Set.of(user.getRole());

        return new UserPrincipal(
                user,
                roles,
                user.getRole()
                        .getPermissions()
                        .stream()
                        .map(Enum::name)
                        .collect(Collectors.toSet())
        );
    }
}
