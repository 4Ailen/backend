package com.aliens.friendship.domain.auth.repository;

import com.aliens.friendship.domain.auth.token.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RefreshTokenRepository
        extends MongoRepository<RefreshToken, String> {

    boolean existsByEmail(String email);
    Optional<RefreshToken> findByEmailAndValue(String email, String value);

    void deleteAllByEmail(String email);
}