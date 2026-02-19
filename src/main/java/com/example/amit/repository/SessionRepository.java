package com.example.amit.repository;


import com.example.amit.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface SessionRepository extends JpaRepository<Session, UUID> {

    Optional<Session> findBySessionToken(String sessionToken);

    List<Session> findByUserIdAndIsActiveTrueOrderByCreatedAtDesc(UUID UserId);


    Optional<Session> findFirstByUserIdAndIsActiveTrueOrderByCreatedAtDesc(UUID UserId);

    @Modifying
    @Query("UPDATE Session s SET s.isActive = false WHERE s.user.id = :UserId AND s.sessionToken != :currentToken")
    void deactivateOtherSessions(UUID UserId, String currentToken);

    @Modifying
    @Query("UPDATE Session s SET s.isActive = false WHERE s.user.id = :UserId")
    void deactivateAllSessions(UUID UserId);

    @Modifying
    @Query("UPDATE Session s SET s.isActive = false WHERE s.expiresAt < :now")
    void deactivateExpiredSessions(LocalDateTime now);

    @Query("SELECT COUNT(s) FROM Session s WHERE s.user.id = :UserId AND s.isActive = true")
    long countActiveSessionsByUserId(UUID UserId);

    List<Session> findSessionByUserId(UUID UserId);
}
