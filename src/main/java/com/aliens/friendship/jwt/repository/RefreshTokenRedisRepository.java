package com.aliens.friendship.jwt.repository;

import com.aliens.friendship.jwt.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
}