package com.aliens.friendship.domain.jwt.repository;

import com.aliens.friendship.domain.jwt.domain.LogoutAccessToken;
import org.springframework.data.repository.CrudRepository;

public interface LogoutAccessTokenRedisRepository extends CrudRepository<LogoutAccessToken, String> {
}