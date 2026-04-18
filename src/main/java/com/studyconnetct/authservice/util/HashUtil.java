package com.studyconnetct.authservice.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HashUtil {
    
    /**
     * Hash password using SHA-256
     */
    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error hashing password", e);
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verify password against hash
     */
    public boolean verifyPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
    
    /**
     * Hash token using SHA-256
     */
    public String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("Error hashing token", e);
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
