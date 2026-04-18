package com.studyconnetct.authservice.repository;

import com.studyconnetct.authservice.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHashAndIsRevokedFalse(String tokenHash);
    void deleteByUserId(UUID userId);
}
