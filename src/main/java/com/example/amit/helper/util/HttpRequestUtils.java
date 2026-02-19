package com.example.amit.helper.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.regex.Pattern;


@Slf4j
@NoArgsConstructor
public final class HttpRequestUtils {

    private static final String[] IP_HEADER_CANDIDATES = {
            "X-Forwarded-For",
            "X-Real-IP",
            "CF-Connecting-IP",
            "X-Client-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };


    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"
    );


    private static final Pattern IPV6_PATTERN = Pattern.compile(
            "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$|^::1$|^::$"
    );

    private static final String UNKNOWN_IP = "unknown";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";


    public static Optional<HttpServletRequest> getCurrentRequest() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                return Optional.of(((ServletRequestAttributes) requestAttributes).getRequest());
            }
        } catch (Exception e) {
            log.debug("Failed to get current request: {}", e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Legacy method for backward compatibility.
     * @deprecated Use {@link #getCurrentRequest()} instead
     */
    @Deprecated
    public static HttpServletRequest currentRequest() {
        Optional<HttpServletRequest> requestOpt = getCurrentRequest();

        if (requestOpt.isEmpty()) {
            log.warn("Legacy call to currentRequest() returned null. Please migrate to getCurrentRequest() for safe Optional handling.");
        }

        return requestOpt.orElse(null);
    }


    public static String getClientIpAddress() {
        return getCurrentRequest()
                .map(HttpRequestUtils::extractClientIpFromRequest)
                .orElse("0.0.0.0");
    }


    private static String extractClientIpFromRequest(HttpServletRequest request) {
        return extractClientIpInfoFromRequest(request).ipAddress();
    }

    private static ClientIpInfo extractClientIpInfoFromRequest(HttpServletRequest request) {
        log.debug("Extracting client IP from request. Remote address: {}", request.getRemoteAddr());

        log.info("=== DEBUG: All Request Headers ===");
        java.util.Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            log.info("Header: {} = {}", headerName, request.getHeader(headerName));
        }
        log.info("=== END Headers ===");

        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor) && !UNKNOWN_IP.equalsIgnoreCase(xForwardedFor.trim())) {
            log.debug("X-Forwarded-For header: {}", xForwardedFor);
            String[] ips = xForwardedFor.split(",");
            String clientIp = ips[0].trim();
            if (isValidIpAddress(clientIp)) {
                log.info("Using client IP from X-Forwarded-For: {}", clientIp);
                return new ClientIpInfo(clientIp, "X-Forwarded-For");
            }
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.hasText(xRealIp) && !UNKNOWN_IP.equalsIgnoreCase(xRealIp.trim())) {
            log.debug("X-Real-IP header: {}", xRealIp);
            if (isValidIpAddress(xRealIp)) {
                log.info("Using client IP from X-Real-IP: {}", xRealIp);
                return new ClientIpInfo(xRealIp, "X-Real-IP");
            }
        }
        for (String header : IP_HEADER_CANDIDATES) {
            if ("X-Forwarded-For".equals(header) || "X-Real-IP".equals(header)) {
                continue;
            }
            String ipList = request.getHeader(header);
            if (StringUtils.hasText(ipList) && !UNKNOWN_IP.equalsIgnoreCase(ipList.trim())) {
                log.debug("Found header {}: {}", header, ipList);
                String[] ips = ipList.split(",");
                for (String ip : ips) {
                    String cleanIp = ip.trim();
                    if (isValidIpAddress(cleanIp) && !isPrivateOrLocalIp(cleanIp)) {
                        log.debug("Using public IP {} from header {}", cleanIp, header);
                        return new ClientIpInfo(cleanIp, header);
                    }
                }
            }
        }

        String remoteAddr = request.getRemoteAddr();
        if (StringUtils.hasText(remoteAddr) && isValidIpAddress(remoteAddr)) {
            if (remoteAddr.startsWith("172.")) {
                log.warn("WARNING: Using Docker bridge IP {}. Check if proxy headers are being sent correctly.", remoteAddr);
            }
            String source = isPrivateOrLocalIp(remoteAddr) ? "REMOTE_ADDR_LOCAL" : "REMOTE_ADDR";
            log.debug("Using remote address: {} (source: {})", remoteAddr, source);
            return new ClientIpInfo(remoteAddr, source);
        }

        log.warn("Unable to determine client IP address");
        return new ClientIpInfo("0.0.0.0", "UNKNOWN");
    }

    private static boolean isValidIpAddress(String ip) {
        if (!StringUtils.hasText(ip)) {
            return false;
        }

        if (IPV4_PATTERN.matcher(ip).matches()) {
            return true;
        }

        if (ip.contains(":") && (IPV6_PATTERN.matcher(ip).matches() ||
                LOCALHOST_IPV6.equals(ip) || "::1".equals(ip))) {
            return true;
        }

        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    private static boolean isPrivateOrLocalIp(String ip) {
        if (!StringUtils.hasText(ip)) {
            return true;
        }

        if (LOCALHOST_IPV4.equals(ip) || LOCALHOST_IPV6.equals(ip) || "::1".equals(ip)) {
            return true;
        }

        if (IPV4_PATTERN.matcher(ip).matches()) {
            String[] parts = ip.split("\\.");
            int firstOctet = Integer.parseInt(parts[0]);
            int secondOctet = Integer.parseInt(parts[1]);

            if (firstOctet == 10) return true;
            if (firstOctet == 172 && secondOctet >= 16 && secondOctet <= 31) return true;
            if (firstOctet == 192 && secondOctet == 168) return true;
        }

        return false;
    }

    public static Optional<String> getAuthenticatedUsername() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (isValidAuthentication(auth) && StringUtils.hasText(auth.getName()) && !"anonymousUser".equalsIgnoreCase(auth.getName())) {
                return Optional.of(auth.getName());
            }
        } catch (Exception e) {
            log.debug("Failed to get authenticated username: {}", e.getMessage());
        }
        return Optional.empty();
    }

    public static String getUserAgent() {
        return getCurrentRequest()
                .map(request -> {
                    String userAgent = request.getHeader("User-Agent");
                    return StringUtils.hasText(userAgent) ? userAgent : request.getHeader("user-agent");
                })
                .filter(StringUtils::hasText)
                .orElse("UNKNOWN");
    }

    private static boolean isValidAuthentication(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }

    private record ClientIpInfo(String ipAddress, String source) { }
}
