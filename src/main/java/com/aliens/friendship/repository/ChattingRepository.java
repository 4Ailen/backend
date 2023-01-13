package com.aliens.friendship.repository;

import com.aliens.friendship.domain.Chatting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRepository extends JpaRepository<Chatting, Integer> {
}