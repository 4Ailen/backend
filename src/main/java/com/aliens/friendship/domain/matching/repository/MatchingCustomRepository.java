package com.aliens.friendship.domain.matching.repository;

import com.aliens.friendship.domain.chatting.domain.ChattingRoom;
import com.aliens.friendship.domain.matching.domain.Applicant;

import java.util.List;

public interface MatchingCustomRepository {
    List<Integer> findPartnerIdsByApplicantId(Integer applicantId);
    Integer findPartnerIdByApplicantAndChattingRoom(Applicant applicant, ChattingRoom chattingRoom);
}