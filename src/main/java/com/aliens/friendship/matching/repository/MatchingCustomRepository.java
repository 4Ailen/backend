package com.aliens.friendship.matching.repository;

import com.aliens.friendship.chatting.domain.ChattingRoom;
import com.aliens.friendship.matching.domain.Applicant;
import com.aliens.friendship.member.domain.Member;

import java.util.List;

public interface MatchingCustomRepository {
    List<Integer> findPartnerIdsByApplicantId(Integer applicantId);
    Integer findPartnerIdByApplicantAndChattingRoom(Applicant applicant, ChattingRoom chattingRoom);
}