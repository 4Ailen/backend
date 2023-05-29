package com.aliens.friendship.domain.auth.repository;

import com.aliens.friendship.domain.auth.token.RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefreshTokenRepository
        extends MongoRepository<RefreshToken, String> {
}