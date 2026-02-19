package com.example.amit.modules.authentication.service;


import com.example.amit.helper.util.HttpRequestUtils;
import com.example.amit.models.Session;
import com.example.amit.models.User;
import com.example.amit.repository.SessionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@AllArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
//    private final EventService eventService;

    @Transactional
    public void createSession(User user, String sessionToken, Instant expiresAt) {
        if (user.isSingleSessionOnly()) {
            deactivateOtherUserSessions(user, sessionToken);
        }

        final String userAgent = HttpRequestUtils.getUserAgent();
        final String ipAddress = HttpRequestUtils.getClientIpAddress();


        Session savedSession = sessionRepository.save(Session.builder()
                .user(user)
                .sessionToken(sessionToken)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .deviceFingerprint(generateDeviceFingerprint(userAgent, ipAddress))
                .lastAccessedAt(Instant.now())
                .expiresAt(expiresAt)
                .build());

        log.info("Created new session for user: {} from IP: {}", user.getEmail(), savedSession.getIpAddress());
//        eventService.publishNewSessionCreatedEvent(user);
    }

    @Transactional
    public void deactivateOtherUserSessions(User User, String currentToken) {
        sessionRepository.deactivateOtherSessions(User.getId(), currentToken);
        log.info("Deactivated other sessions for user: {}", User.getEmail());
    }

    @Transactional
    public void deactivateAllSessions(User User) {
        sessionRepository.deactivateAllSessions(User.getId());
        log.info("Deactivated all sessions for user: {}", User.getEmail());
    }

    @Transactional
    public void deactivateSession(String sessionToken) {
        Optional<Session> session = sessionRepository.findBySessionToken(sessionToken);
        if (session.isPresent()) {
            Session userSession = session.get();
            userSession.setActive(false);
            sessionRepository.save(userSession);
            log.info("Deactivated session for user: {}", userSession.getUser().getEmail());
        }
    }

    public boolean isSessionActive(String sessionToken) {
        Optional<Session> session = sessionRepository.findBySessionToken(sessionToken);
        return session.isPresent() && session.get().isActive() &&
                session.get().getExpiresAt().isAfter(Instant.now());
    }

    @Transactional
    public void updateSessionAccess(String sessionToken) {
        Optional<Session> session = sessionRepository.findBySessionToken(sessionToken);
        if (session.isPresent() && session.get().isActive()) {
            Session userSession = session.get();
            userSession.setLastAccessedAt(Instant.now());
            sessionRepository.save(userSession);
        }
    }

    public List<Session> getAllUserSessions(UUID userId) {
        return sessionRepository.findSessionByUserId(userId);
    }

    public List<Session> getActiveUserSessions(UUID userId) {
        return sessionRepository.findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(userId);
    }

    public Optional<Session> getUserLastSession(UUID UserId) {
        return sessionRepository.findFirstByUserIdAndIsActiveTrueOrderByCreatedAtDesc(UserId);
    }

    public long getActiveSessionCount(UUID userId) {
        return sessionRepository.countActiveSessionsByUserId(userId);
    }

    private String generateDeviceFingerprint(String userAgent, String ipAddress) {
        return String.valueOf((userAgent + ipAddress).hashCode());
    }

    @Transactional
    @Scheduled(fixedRate = 3600000)
    public void cleanupExpiredSessions() {
        sessionRepository.deactivateExpiredSessions(LocalDateTime.now());
        log.info("Cleaned up expired sessions");
    }
}
