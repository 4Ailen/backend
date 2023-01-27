package com.aliens.friendship.chattingRoom.repository;

import com.aliens.friendship.chattingRoom.domain.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Integer> {
}