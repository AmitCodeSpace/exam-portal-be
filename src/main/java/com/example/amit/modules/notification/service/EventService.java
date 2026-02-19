//package com.example.amit.modules.notification.service;
//
//
//import lombok.AllArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Service;
//
//
//@Slf4j
//@Service
//@AllArgsConstructor
//public class EventService {
//
//    private final NotificationService notificationService;
//
//    public void publishFailedLoginEvent(Employee employee) {
//        notificationService.notifySelf(
//                employee,
//                "Login Failed",
//                "Failed login attempt for user '%s'".formatted(employee.getUsername())
//        );
//    }
//
//
//    public void publishAccountLockedEvent(Employee employee) {
//        notificationService.notifySelf(
//                employee,
//                "Account Locked",
//                "Your account has been locked due to multiple failed login attempts"
//        );
//    }
//
//    public void publishForgotPasswordEvent(Employee employee) {
//        notificationService.notifySelf(
//                employee,
//                "Forgot Password",
//                "Forgot password process completed for username: %s".formatted(employee.getUsername())
//        );
//    }
//
//    public void publishNewSessionCreatedEvent(Employee employee) {
//        notificationService.notifySelf(
//                employee,
//                "New Session Created",
//                "New session created for user %s after successful login".formatted(employee.getUsername())
//        );
//    }
//
//    public void publishNewEmployeeCreatedEvent(Employee employee, Authentication authentication) {
//        notificationService.notifySelf(
//                employee,
//                "New Employee Created",
//                "New User Created with username %s by %s".formatted(employee.getUsername(), authentication.getName())
//        );
//    }
//}
