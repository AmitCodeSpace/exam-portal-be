//package com.example.amit.modules.notification.dto.response;
//
//import com.skytelteleservice.hrms.models.Notification;
//import com.skytelteleservice.hrms.models.NotificationRecipient;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//public record NotificationItemResponse(
//        UUID recipientId,
//        UUID notificationId,
//        UUID actorId,
//        String actorUsername,
//        String title,
//        String message,
//        boolean seen,
//        LocalDateTime timestamp
//) {
//    public static NotificationItemResponse from(NotificationRecipient nr) {
//        Notification n = nr.getNotification();
//
//        return new NotificationItemResponse(
//                nr.getId(),
//                n.getId(),
//                n.getActor() != null ? n.getActor().getId() : null,
//                n.getActor() != null ? n.getActor().getUsername() : null,
//                n.getTitle(),
//                n.getMessage(),
//                nr.isSeen(),
//                n.getCreatedAt()
//        );
//    }
//}
