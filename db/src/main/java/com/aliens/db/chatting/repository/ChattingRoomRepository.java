package com.aliens.db.chatting.repository;

import com.aliens.db.chatting.entity.ChattingRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChattingRoomRepository extends JpaRepository<ChattingRoomEntity, Long> {
    Optional<ChattingRoomEntity> findTopByOrderByCreatedAtDesc();

    //           findFirst1ByTutoringOrderByDateTimeDesc
}