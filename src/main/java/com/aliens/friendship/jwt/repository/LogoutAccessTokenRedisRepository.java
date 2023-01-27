package com.aliens.friendship.jwt.repository;

import com.aliens.friendship.jwt.domain.LogoutAccessToken;
import org.springframework.data.repository.CrudRepository;

public interface LogoutAccessTokenRedisRepository extends CrudRepository<LogoutAccessToken, String> {
}