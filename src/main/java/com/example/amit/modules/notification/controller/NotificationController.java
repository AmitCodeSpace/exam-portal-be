//package com.example.amit.modules.notification.controller;
//
//
//import com.example.amit.common.ApiResponse;
//import com.example.amit.modules.notification.dto.response.NotificationPageResponse;
//import com.example.amit.modules.notification.service.NotificationService;
//import com.example.amit.security.service.UserPrincipal;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Map;
//import java.util.UUID;
//
//
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/v1/notifications")
//public class NotificationController {
//
//    private final NotificationService notificationService;
//
//    @GetMapping("/self")
//    public ResponseEntity<ApiResponse<NotificationPageResponse>> self(
//            @AuthenticationPrincipal UserPrincipal principal,
//            @RequestParam(defaultValue = "false") boolean unseenOnly,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return ResponseEntity.ok(
//                ApiResponse.success(
//                        notificationService.getSelfActivities(principal, unseenOnly, page, size),
//                        "Fetched logged-in employee activities successfully",
//                        HttpStatus.OK
//                )
//        );
//    }
//
//
//    @GetMapping("/others")
//    public ResponseEntity<ApiResponse<NotificationPageResponse>> others(
//            @AuthenticationPrincipal UserPrincipal principal,
//            @RequestParam(defaultValue = "false") boolean unseenOnly,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "10") int size
//    ) {
//        return ResponseEntity.ok(
//                ApiResponse.success(
//                        notificationService.getOtherNotifications(principal, unseenOnly, page, size),
//                        "Fetched logged-in employee notifications successfully",
//                        HttpStatus.OK
//                )
//        );
//    }
//
//    @GetMapping("/unseen-count")
//    public ResponseEntity<ApiResponse<Map<String, Long>>> unseenCount(@RequestParam String type, @AuthenticationPrincipal EmployeePrincipal principal) {
//        return ResponseEntity
//                .ok(ApiResponse.success(notificationService.unseenCount(type, principal), "Fetch loggedIn employee unseen notification count successfully.", HttpStatus.OK));
//    }
//
//    @PutMapping("/{recipientId}/seen")
//    public ResponseEntity<ApiResponse<Object>> markSingle(@PathVariable UUID recipientId, @AuthenticationPrincipal EmployeePrincipal principal) {
//        notificationService.markSingleAsSeen(recipientId, principal.getUsername());
//
//        return ResponseEntity.ok(ApiResponse.success("Notification marked as seen", HttpStatus.OK));
//    }
//
//    @PutMapping("/seen/self")
//    public ResponseEntity<ApiResponse<Object>> markSelf(@AuthenticationPrincipal UserPrincipal principal) {
//        long updated = notificationService.markSelfAsSeen(principal);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(Map.of("updated", updated), "Self activities marked as seen", HttpStatus.OK));
//    }
//
//    @PutMapping("/seen/others")
//    public ResponseEntity<ApiResponse<Object>> markOthers(@AuthenticationPrincipal UserPrincipal principal) {
//        long updated = notificationService.markOthersAsSeen(principal);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(Map.of("updated", updated), "Other notifications marked as seen", HttpStatus.OK));
//    }
//
//    @PutMapping("/seen/all")
//    public ResponseEntity<ApiResponse<Object>> markAll(@AuthenticationPrincipal UserPrincipal principal) {
//        long updated = notificationService.markAllAsSeen(principal);
//
//        return ResponseEntity.ok(
//                ApiResponse.success(Map.of("updated", updated), "All notifications marked as seen", HttpStatus.OK));
//    }
//}
