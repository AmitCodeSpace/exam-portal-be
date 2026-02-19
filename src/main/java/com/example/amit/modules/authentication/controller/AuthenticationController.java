package com.example.amit.modules.authentication.controller;


import com.example.amit.modules.authentication.dto.response.AuthenticationResponse;
import com.example.amit.modules.authentication.dto.request.VerificationRequest;
import com.example.amit.modules.authentication.service.AuthenticationService;
import com.example.amit.modules.authentication.dto.request.RegisterRequest;
import com.example.amit.modules.authentication.dto.request.LoginRequest;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.example.amit.common.ApiResponse;
import lombok.RequiredArgsConstructor;




@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    // Here ADMIN Can Only Create  Endpoint For All
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> register(@RequestBody RegisterRequest request, HttpServletResponse res) {
        var response = service.register(request, res);
        if (request.mfaEnabled()) return ResponseEntity.ok(ApiResponse.success(response, "Register Success..", HttpStatus.CREATED));
        return ResponseEntity.accepted().body(ApiResponse.success("Register Success..", HttpStatus.CREATED));
    }

    // This Auth Endpoint For All
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> authenticate(@RequestBody LoginRequest request, HttpServletResponse res) {
        System.out.println("Authenticating request: " + request);
        return ResponseEntity.ok(ApiResponse.success(service.login(request, res), "Authentication success", HttpStatus.OK));
    }

//    @PostMapping("/refresh-token")
//    public ResponseEntity<ApiResponse<Object>> refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        service.refreshToken(request, response);
//        return ResponseEntity.ok().body(ApiResponse.success( "Refresh token generate success", HttpStatus.OK));
//    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> verifyCode(@RequestBody VerificationRequest request, HttpServletResponse res) {
        return ResponseEntity.ok().body(ApiResponse.success(
                service.verifyCode(request, res), "", HttpStatus.OK));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Object>> verifyCode(HttpServletResponse res) {
        service.logout(res);
        return ResponseEntity.ok().body(ApiResponse.success("Logout success", HttpStatus.OK));
    }
}
