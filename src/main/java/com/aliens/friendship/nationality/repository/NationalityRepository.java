package com.aliens.friendship.nationality.repository;

import com.aliens.friendship.nationality.domain.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NationalityRepository extends JpaRepository<Nationality, Integer> {
}