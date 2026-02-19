package com.example.amit.modules.demo.controller;

import com.example.amit.common.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class AdminController {

    @GetMapping
    @PreAuthorize("hasAuthority('system_admin:read')")
    public ResponseEntity<ApiResponse<String>> get() {
        return ResponseEntity.ok(ApiResponse.success("GET:: admin controller", "Super_Admin get success", HttpStatus.OK));
    }

    @Hidden
    @PostMapping
    @PreAuthorize("hasAuthority('admin:create')")
    public String post() {
        return "POST:: admin controller";
    }

    @Hidden
    @PutMapping
    @PreAuthorize("hasAuthority('admin:update')")
    public String put() {
        return "PUT:: admin controller";
    }

    @Hidden
    @DeleteMapping
    @PreAuthorize("hasAuthority('admin:delete')")
    public String delete() {
        return "DELETE:: admin controller";
    }
}
