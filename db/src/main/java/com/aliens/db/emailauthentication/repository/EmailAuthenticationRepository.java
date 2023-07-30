package com.aliens.db.emailauthentication.repository;

import com.aliens.db.emailauthentication.entity.EmailAuthenticationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthenticationRepository extends JpaRepository<EmailAuthenticationEntity, Integer> {
    EmailAuthenticationEntity findByEmail(String email);

    void deleteByEmail(String email);

    boolean existsByEmail(String email);
}