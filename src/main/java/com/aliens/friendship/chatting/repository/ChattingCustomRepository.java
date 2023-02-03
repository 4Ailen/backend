package com.aliens.friendship.chatting.repository;

import com.aliens.friendship.chatting.domain.ChattingRoom;
import com.aliens.friendship.matching.domain.MatchingParticipant;

public interface ChattingCustomRepository {

    Integer findMatchingParticipantIdByMatchingParticipantAndChattingRoom(MatchingParticipant matchingParticipant, ChattingRoom chattingRoom);
}
