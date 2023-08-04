package com.aliens.db.auth.repository;

import com.aliens.db.auth.entity.FcmTokenEntity;
import com.aliens.db.auth.entity.RefreshTokenEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FcmTokenRepository extends MongoRepository<FcmTokenEntity, String> {
    boolean existsByValue(String value);
    void deleteAllByValue(String value);
}