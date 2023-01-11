package com.aliens.friendship.repository;

import com.aliens.friendship.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRedisRepository extends CrudRepository<RefreshToken, String> {
}