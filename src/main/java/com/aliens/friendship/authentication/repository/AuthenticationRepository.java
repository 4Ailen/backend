package com.aliens.friendship.authentication.repository;

import com.aliens.friendship.authentication.domain.Authentication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<Authentication, Integer> {
}