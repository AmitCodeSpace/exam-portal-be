//package com.example.amit.helper.validation;
//
//import com.skytelteleservice.hrms.exception.AccessDeniedException;
//import com.skytelteleservice.hrms.exception.AlreadyExistsException;
//import com.skytelteleservice.hrms.exception.BadRequestException;
//import com.skytelteleservice.hrms.models.Employee;
//import com.skytelteleservice.hrms.models.Role;
//import com.skytelteleservice.hrms.repository.EmployeeRepository;
//import com.skytelteleservice.hrms.repository.RoleRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.Set;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//
//@Component
//@RequiredArgsConstructor
//public class EmployeeValidator {
//
//    private final EmployeeRepository employeeRepository;
//    private final RoleRepository roleRepository;
//    private final PasswordEncoder passwordEncoder;
//
//
//    public void validateUniqueUsername(String username) {
//        if (employeeRepository.existsByUsername(username.trim())) {
//            throw new AlreadyExistsException("User with username: " + username + " already exists");
//        }
//    }
//
//    public void validateUniqueEmail(String email) {
//        if (employeeRepository.existsByEmail(email.trim())) {
//            throw new AlreadyExistsException("User with email: " + email + " already exists");
//        }
//    }
//
//    public Set<Role> validateAndFetchRoles(Set<UUID> roleIds) {
//        Set<Role> roles = roleRepository.findByIdIn(roleIds);
//        if (roles.size() != roleIds.size()) throw new BadRequestException("One or more roles are invalid");
//
//        return roles;
//    }
//
//    public void validateUpdateAccess(Employee actingUser, boolean isSelfUpdate) {
//        if (!isSelfUpdate && !actingUser.isSystemAdmin()) {
//            throw new AccessDeniedException("Access Denied: You do not have access to update this user profile !!");
//        }
//    }
//
//    public void validateChangePassword(String currentPassword, String newPassword, Employee targetUser) {
//        if (currentPassword == null || !passwordEncoder.matches(currentPassword, targetUser.getPassword())) {
//            throw new BadRequestException("Incorrect current password !!");
//        }
//
//        if(newPassword != null && newPassword.length() < 8) {
//            throw new BadRequestException("Password must be at least 8 characters");
//        }
//
//        if (passwordEncoder.matches(newPassword, targetUser.getPassword())) {
//            throw new BadRequestException("New password must be different from the current password");
//        }
//    }
//    public void validateRestrictedUserAccessUpdate(Employee actingUser, Employee targetUser) {
//        if (!targetUser.isRegularEmployee() && !actingUser.isSystemAdmin()) {
//            throw new AccessDeniedException("Cannot update permissions or status for non-regular users");
//        }
//    }
//
//    public void validateRoleUpdateAccess(Employee actingUser, boolean isSelfUpdate) {
//        if (isSelfUpdate) throw new AccessDeniedException("You cannot change your own roles");
//
//        if (!actingUser.isSystemAdmin()) throw new AccessDeniedException("Only system admin can update roles");
//    }
//
//
//    public boolean rolesChanged(Set<Role> existingRoles, Set<Role> newRoles) {
//        if (existingRoles.size() != newRoles.size()) return true;
//
//        Set<UUID> existingIds = existingRoles.stream().map(Role::getId).collect(Collectors.toSet());
//        Set<UUID> newIds = newRoles.stream().map(Role::getId).collect(Collectors.toSet());
//
//        return !existingIds.equals(newIds);
//    }
//}
