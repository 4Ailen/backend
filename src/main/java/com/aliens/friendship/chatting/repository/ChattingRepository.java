package com.aliens.friendship.chatting.repository;

import com.aliens.friendship.chatting.domain.Chatting;
import com.aliens.friendship.chatting.domain.ChattingRoom;
import com.aliens.friendship.matching.domain.MatchingParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChattingRepository extends JpaRepository<Chatting, Integer> {

    List<Chatting> findByMatchingParticipant(MatchingParticipant matchingParticipant);

    List<Chatting> findByChattingRoom(ChattingRoom chattingRoom);
}