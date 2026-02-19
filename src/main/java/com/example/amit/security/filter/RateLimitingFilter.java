package com.example.amit.security.filter;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final String[] EXCLUDED_PATHS = {"/actuator/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**"};
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final int MAX_REQUESTS_PER_MINUTES = 250;

    private final Cache<String, Counter> cache =
            Caffeine.newBuilder()
                    .expireAfterWrite(1, TimeUnit.MINUTES)
                    .maximumSize(100_000)
                    .build();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return Arrays.stream(EXCLUDED_PATHS)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws IOException, ServletException {

        String clientIp = resolveClientIp(request);

        Counter counter = cache.get(clientIp, ip -> new Counter());

        if (counter.increment() > MAX_REQUESTS_PER_MINUTES) {
            sendTooManyRequests(request, response);
            return;
        }

        chain.doFilter(request, response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return forwarded != null ? forwarded.split(",")[0] : request.getRemoteAddr();
    }

    private void sendTooManyRequests(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setStatus(429);
        response.setContentType("application/json");
        response.getWriter().write("""
            {
              "path": "%s",
              "error": "TOO MANY REQUESTS",
              "status": 429,
              "message": "Too many requests. Please try again later.",
              "timestamp": "%s"
            }
        """.formatted(request.getServletPath(), Instant.now()));
    }


    private static class Counter {
        private final AtomicInteger count = new AtomicInteger(0);

        int increment() {
            return count.incrementAndGet();
        }
    }
}
