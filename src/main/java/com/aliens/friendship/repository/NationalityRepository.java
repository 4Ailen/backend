package com.aliens.friendship.repository;

import com.aliens.friendship.domain.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NationalityRepository extends JpaRepository<Nationality, Integer> {
}