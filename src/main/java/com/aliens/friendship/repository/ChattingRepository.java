package com.aliens.friendship.repository;

import com.aliens.friendship.domain.Chatting;
import com.aliens.friendship.domain.ChattingRoom;
import com.aliens.friendship.domain.MatchingParticipant;
import com.aliens.friendship.domain.dto.RoomInfoDto;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChattingRepository extends JpaRepository<Chatting, Integer> {

    List<Chatting> findByMatchingParticipant(MatchingParticipant matchingParticipant);

    List<Chatting> findByChattingRoom(ChattingRoom chattingRoom);
}