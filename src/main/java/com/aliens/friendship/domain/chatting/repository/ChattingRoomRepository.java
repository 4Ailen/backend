package com.aliens.friendship.domain.chatting.repository;

import com.aliens.friendship.domain.chatting.domain.ChattingRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoom, Long> {
}