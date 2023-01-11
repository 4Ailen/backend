package com.aliens.friendship.repository;

import com.aliens.friendship.domain.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Integer> {
}