package com.aliens.friendship.member.repository;

import com.aliens.friendship.member.domain.Nationality;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NationalityRepository extends JpaRepository<Nationality, Integer> {
}