package com.aliens.db.auth.repository;

import com.aliens.db.auth.entity.FcmTokenEntity;
import com.aliens.db.auth.entity.RefreshTokenEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FcmTokenRepository extends MongoRepository<FcmTokenEntity, String> {
    void deleteAllByValue(String value);
    FcmTokenEntity findByValue(String fcmToken);
    List<FcmTokenEntity> findAllByMemberId(Long memberId);
}