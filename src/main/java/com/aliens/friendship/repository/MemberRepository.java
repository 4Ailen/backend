package com.aliens.friendship.repository;

import com.aliens.friendship.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
}