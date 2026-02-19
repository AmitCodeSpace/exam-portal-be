package com.example.amit.repository;

import com.example.amit.models.NotificationRecipient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;


public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, UUID> {

    @Query("SELECT nr FROM NotificationRecipient nr JOIN nr.notification n WHERE nr.user.id = :userId AND n.actor.id = :userId")
    Page<NotificationRecipient> findSelfActivities(UUID userId, Pageable pageable);

    @Query("SELECT nr FROM NotificationRecipient nr JOIN nr.notification n WHERE nr.user.id = :userId AND n.actor.id = :userId AND nr.seen = false")
    Page<NotificationRecipient> findSelfActivitiesUnseen(UUID userId, Pageable pageable);


    @Query(" SELECT nr FROM NotificationRecipient nr JOIN nr.notification n WHERE nr.user.id = :userId AND (n.actor IS NULL OR n.actor.id <> :userId)")
    Page<NotificationRecipient> findOtherNotifications(UUID userId, Pageable pageable);

    @Query("""
            SELECT nr
            FROM NotificationRecipient nr
            JOIN nr.notification n
                WHERE nr.user.id = :userId
                AND (n.actor IS NULL OR n.actor.id <> :userId)
                AND nr.seen = false
    """)
    Page<NotificationRecipient> findOtherNotificationsUnseen(
            UUID userId,
            Pageable pageable
    );


    @Query("""
                SELECT COUNT(nr)
                FROM NotificationRecipient nr
                JOIN nr.notification n
                WHERE nr.user.id = :userId
                  AND n.actor.id = :userId
                  AND nr.seen = false
            """)
    long countUnseenSelfActivities(UUID userId);

    @Query("""
                SELECT COUNT(nr)
                FROM NotificationRecipient nr
                JOIN nr.notification n
                WHERE nr.user.id = :userId
                  AND (n.actor IS NULL OR n.actor.id <> :userId)
                  AND nr.seen = false
            """)
    long countUnseenOtherNotifications(UUID userId);


    @Modifying
    @Query("""
                UPDATE NotificationRecipient nr
                SET nr.seen = true
                WHERE nr.user.id = :userId
                  AND nr.seen = false
            """)
    int markAllAsSeen(UUID userId);

    @Modifying
    @Query("""
            UPDATE NotificationRecipient nr
            SET nr.seen = true
            WHERE nr.user.id = :userId
               AND nr.seen = false
               AND nr.notification.actor.id = :userId
       """)
    int markSelfAsSeen(UUID userId);

    @Modifying
    @Query("""
            UPDATE NotificationRecipient nr
            SET nr.seen = true
            WHERE nr.user.id = :userId
               AND nr.seen = false
               AND (nr.notification.actor IS NULL
                   OR nr.notification.actor.id <> :userId)
        """)
    int markOthersAsSeen(UUID userId);
}

