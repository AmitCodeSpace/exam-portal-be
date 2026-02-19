//package com.example.amit.modules.notification.service;
//
//
//import com.example.amit.helper.builder.ApiResponseBuilder;
//import com.example.amit.models.Notification;
//import com.example.amit.models.NotificationRecipient;
//import com.example.amit.models.User;
//import com.example.amit.modules.notification.dto.response.NotificationPageResponse;
//import com.example.amit.repository.NotificationRecipientRepository;
//import com.example.amit.repository.NotificationRepository;
//import com.example.amit.security.service.UserPrincipal;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class NotificationService {
//
//    private final NotificationRecipientRepository recipientRepository;
//    private final NotificationRepository notificationRepository;
//
//
//    public void notifySelf(User User, String title, String message) {
//        notifyUsers(User, Set.of(User), title, message);
//    }
//
//    public void notifySpecificUsers(User actor, Set<User> targetUsers, String title, String message) {
//        notifyUsers(actor, targetUsers, title, message);
//    }
//
//    public void notifySuperior(User actor, String title, String message) {
//        if (actor.getSuperior() == null) return;
//
//        notifyUsers(actor, Set.of(actor.getSuperior()), title, message);
//    }
//
//    public void notifySuperiors(User actor, String title, String message) {
//        Set<User> superiors = Stream
//                .iterate(actor.getSuperior(), Objects::nonNull, User::getSuperior)
//                .collect(Collectors.toUnmodifiableSet());
//
//        notifyUsers(actor, superiors, title, message);
//    }
//
//    private void notifyUsers(User actor, Set<User> targetUsers, String title, String message) {
//
//        Notification notification = Notification.builder()
//                .title(title)
//                .message(message)
//                .actor(actor)
//                .build();
//
//        targetUsers.stream()
//                .map(User -> NotificationRecipient.builder()
//                        .notification(notification)
//                        .User(User)
//                        .build()
//                )
//                .forEach(notification.getRecipients()::add);
//
//        notificationRepository.save(notification);
//    }
//
//
//    @Transactional(readOnly = true)
//    public NotificationPageResponse getSelfActivities(UserPrincipal principal, boolean unseenOnly, int page, int size) {
//        UUID UserId = principal.User().getId();
//
//        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "notification.createdAt"));
//
//        Page<NotificationRecipient> result =
//                unseenOnly
//                        ? recipientRepository.findSelfActivitiesUnseen(UserId, pageable)
//                        : recipientRepository.findSelfActivities(UserId, pageable);
//
//        return ApiResponseBuilder.toNotificationResponse(result, recipientRepository.countUnseenSelfActivities(UserId));
//    }
//
//    @Transactional(readOnly = true)
//    public NotificationPageResponse getOtherNotifications(UserPrincipal principal, boolean unseenOnly, int page, int size) {
//        UUID UserId = principal.User().getId();
//
//        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "notification.createdAt"));
//        Page<NotificationRecipient> result =
//                unseenOnly
//                        ? recipientRepository.findOtherNotificationsUnseen(UserId, pageable)
//                        : recipientRepository.findOtherNotifications(UserId, pageable);
//
//        return ApiResponseBuilder.toNotificationResponse(result, recipientRepository.countUnseenOtherNotifications(UserId));
//    }
//
//    public Map<String, Long> unseenCount(String type, UserPrincipal principal) {
//        UUID UserId = principal.User().getId();
//
//        Map<String, Long> response = new HashMap<>();
//
//        if ("self".equalsIgnoreCase(type)) {
//            response.put("unseenCount",
//                    recipientRepository.countUnseenSelfActivities(UserId));
//        } else if ("others".equalsIgnoreCase(type)) {
//            response.put("unseenCount",
//                    recipientRepository.countUnseenOtherNotifications(UserId));
//        } else throw new BadRequestException("Invalid type. Allowed values: self, others");
//
//        return response;
//    }
//
//    public long markAllAsSeen(UserPrincipal principal) {
//        return recipientRepository.markAllAsSeen(principal.User().getId());
//    }
//
//    public long markSelfAsSeen(UserPrincipal principal) {
//        return recipientRepository.markSelfAsSeen(principal.User().getId());
//    }
//
//    public long markOthersAsSeen(UserPrincipal principal) {
//        return recipientRepository.markOthersAsSeen(principal.User().getId());
//    }
//
//    public void markSingleAsSeen(UUID recipientId, String username) {
//        NotificationRecipient recipient = recipientRepository.findById(recipientId)
//                .orElseThrow(() -> new NotFoundException("Notification not found"));
//
//        if (!recipient.getUser().getUsername().equals(username))
//            throw new AccessDeniedException("Access denied");
//
//        recipient.setSeen(true);
//    }
//}
