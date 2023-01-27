package com.aliens.friendship.emailAuthentication.repository;

import com.aliens.friendship.emailAuthentication.domain.EmailAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailAuthenticationRepository extends JpaRepository<EmailAuthentication, Integer> {
}