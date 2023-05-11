package com.aliens.friendship.domain.jwt.repository;

import com.aliens.friendship.domain.jwt.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
}