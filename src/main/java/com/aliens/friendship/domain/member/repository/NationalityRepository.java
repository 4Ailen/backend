package com.aliens.friendship.domain.member.repository;

import com.aliens.friendship.domain.member.domain.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NationalityRepository extends JpaRepository<Nationality, Integer> {
}