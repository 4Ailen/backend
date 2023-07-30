package com.aliens.db.auth.repository;

import com.aliens.db.auth.entity.RefreshTokenEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshTokenEntity, String> {

    boolean existsByEmail(String email);
    Optional<RefreshTokenEntity> findByEmailAndValue(String email, String value);
    void deleteAllByEmail(String email);
}