package com.example.amit.security.filter;



import com.example.amit.modules.authentication.service.SessionService;
import com.example.amit.security.service.CustomUserDetailsService;
import com.example.amit.security.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;



@Slf4j
@Order
@Component
@NullMarked
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final SessionService sessionService;

    @Value("${security.unauthenticated-urls}")
    private String[] unauthenticatedUrls;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        final String path = request.getRequestURI();

        if (isUnauthenticatedUrl(request.getRequestURI())) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        final String username;
        final String tokenId;

        try {
            username = jwtService.extractUsername(jwt);
            tokenId = jwtService.extractTokenId(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (!sessionService.isSessionActive(tokenId)) {
                    log.warn("Inactive session for tokenId={} (user={})", tokenId, username);
                    sendUnauthorizedResponse(response, "Session expired or invalid", path);
                    return;
                }

                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    sessionService.updateSessionAccess(tokenId);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token for request path={} reason={}", path, e.getMessage());
            sendUnauthorizedResponse(response, "Token has expired. Please log in again.", path);
            return;
        } catch (MalformedJwtException | SecurityException | SignatureException e) {
            log.warn("JWT validation failed (format/signature issue): {}", e.getMessage());
            sendUnauthorizedResponse(response, "Invalid token signature or format", path);
            return;
        } catch (Exception e) {
            log.error("Unexpected error during JWT validation", e);
            sendUnauthorizedResponse(response, "Authentication failed due to server error", path);
            return;
        }

        filterChain.doFilter(request, response);
    }


    private boolean isUnauthenticatedUrl(String path) {
        return Arrays.stream(unauthenticatedUrls)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String path, String message) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "path": "%s",
              "error": "Unauthorized",
              "status": 401,
              "message": "%s",
              "timestamp": "%s"
            }
        """.formatted(path, message,Instant.now()));
    }
}
