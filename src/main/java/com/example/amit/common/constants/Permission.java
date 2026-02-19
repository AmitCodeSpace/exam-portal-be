package com.example.amit.common.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Permission {
//    ADMIN_READ("admin:read"),
//    ADMIN_UPDATE("admin:update"),
//    ADMIN_CREATE("admin:create"),
//    ADMIN_DELETE("admin:delete"),
//
//    MANAGER_READ("management:read"),
//    MANAGER_UPDATE("management:update"),
//    MANAGER_CREATE("management:create"),
//    MANAGER_DELETE("management:delete"),

    // System and Super Admin Permissions
    SYSTEM_ADMIN_READ("system_admin:read"),
    SYSTEM_ADMIN_UPDATE("system_admin:update"),
    SYSTEM_ADMIN_CREATE("system_admin:create"),
    SYSTEM_ADMIN_DELETE("system_admin:delete"),

    SUPER_ADMIN_READ("super_admin:read"),
    SUPER_ADMIN_UPDATE("super_admin:update"),
    SUPER_ADMIN_CREATE("super_admin:create"),
    SUPER_ADMIN_DELETE("super_admin:delete"),

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),

    MANAGER_READ("management:read"),
    MANAGER_UPDATE("management:update"),
    MANAGER_CREATE("management:create"),
    MANAGER_DELETE("management:delete"),

//    // Admin Manager Permissions
//    ADMIN_MANAGER_READ("admin_manager:read"),
//    ADMIN_MANAGER_UPDATE("admin_manager:update"),
//    ADMIN_MANAGER_CREATE("admin_manager:create"),
//    ADMIN_MANAGER_DELETE("admin_manager:delete"),
//
//    // Developer Manager Permissions
//    DEVELOPER_READ("developer:read"),
//    DEVELOPER_UPDATE("developer:update"),
//    DEVELOPER_CREATE("developer:create"),
//    DEVELOPER_DELETE("developer:delete"),
//
//    // Technical Manager Permissions
//    TECHNICAL_READ("technical:read"),
//    TECHNICAL_UPDATE("technical:update"),
//    TECHNICAL_CREATE("technical:create"),
//    TECHNICAL_DELETE("technical:delete"),
//
//    // Accounts Manager Permissions
//    ACCOUNTS_READ("accounts:read"),
//    ACCOUNTS_UPDATE("accounts:update"),
//    ACCOUNTS_CREATE("accounts:create"),
//    ACCOUNTS_DELETE("accounts:delete"),
//
//    // Sales Manager Permissions
//    SALES_READ("sales:read"),
//    SALES_UPDATE("sales:update"),
//    SALES_CREATE("sales:create"),
//    SALES_DELETE("sales:delete"),
//
//    // HR Manager Permissions
//    HR_READ("hr:read"),
//    HR_UPDATE("hr:update"),
//    HR_CREATE("hr:create"),
//    HR_DELETE("hr:delete"),

    // Employee Permissions
    USER_READ("user:read"),
    USER_UPDATE("user:update"),
    USER_CREATE("user:create"),
    USER_DELETE("user:delete");

    private final String permission;
}
