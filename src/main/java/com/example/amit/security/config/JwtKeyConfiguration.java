package com.example.amit.security.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


@Slf4j
@Configuration
public class JwtKeyConfiguration {

    @Value("${app.jwt.keys.private-key-path}")
    private String privateKeyPath;

    @Value("${app.jwt.keys.public-key-path}")
    private String publicKeyPath;

    @Bean
    public KeyPair jwtKeyPair() {
        try {
            PrivateKey privateKey = loadPrivateKey(privateKeyPath);
            PublicKey publicKey = loadPublicKey(publicKeyPath);

            log.debug("JWT KeyPair loaded successfully.");
            return new KeyPair(publicKey, privateKey);
        } catch (Exception e) {
            log.error("Failed to load JWT keys: {}", e.getMessage());
            throw new IllegalStateException("Failed to initialize JWT keys", e);
        }
    }

    private PrivateKey loadPrivateKey(String path) throws Exception {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);
        }
    }

    private PublicKey loadPublicKey(String path) throws Exception {
        try (InputStream inputStream = new ClassPathResource(path).getInputStream()) {
            String key = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] decoded = Base64.getDecoder().decode(key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);
            return KeyFactory.getInstance("RSA").generatePublic(keySpec);
        }
    }
}
