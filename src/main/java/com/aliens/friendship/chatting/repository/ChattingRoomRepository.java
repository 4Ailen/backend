package com.aliens.friendship.chatting.repository;

import com.aliens.friendship.chatting.domain.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Integer> {
}