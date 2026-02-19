package com.example.amit.security.filter;


import com.example.amit.common.ApiResponse;
import com.example.amit.helper.util.Utility;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;



@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final ApiResponse<Object> errorResponse = ApiResponse.builder()
                .path(request.getServletPath())
                .status(HttpServletResponse.SC_UNAUTHORIZED)
                .error("Unauthorized: Access Denied !!")
                .message(authException.getMessage())
                .timestamp(Instant.now())
                .build();

        Utility.getMapper().writeValue(response.getOutputStream(), errorResponse);
    }
}
