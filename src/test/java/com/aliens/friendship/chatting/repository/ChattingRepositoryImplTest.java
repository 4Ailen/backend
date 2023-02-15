package com.aliens.friendship.chatting.repository;

import com.aliens.friendship.chatting.domain.ChattingRoom;
import com.aliens.friendship.matching.domain.MatchingParticipant;
import com.aliens.friendship.matching.repository.MatchingParticipantRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ChattingRepositoryImplTest {

    @Autowired
    ChattingRepository chattingRepository;

    @Autowired
    private ChattingRoomRepository chattingRoomRepository;

    @Autowired
    private MatchingParticipantRepository matchingParticipantRepository;

    // 현재 db 상황: 9번 채팅룸(멤버 13과 18), 10번 채팅룸(멤버 14와 23)
    @Test
    @DisplayName("특정 1:1 채팅방의 대화 상대방 id 반환 성공")
    void GetPartnerId_When_GivenMatchingParticipantAndChattingRoom() {
        //given: 매칭 신청자와 해당 매칭 신청자가 속해 있는 채팅룸
        MatchingParticipant matchingParticipant = matchingParticipantRepository.findById(13).orElseThrow(() -> new NoSuchElementException("Can't find matchingParticipant " + 13));
        ChattingRoom chattingRoom = chattingRoomRepository.findById(9).orElseThrow(() -> new NoSuchElementException("Can't find chattingRoom " + 9));

        //when: 매칭 신청자가 입장하는 1: 1 채팅방의 대화 상대 id 반환
        Integer partnerId = chattingRepository.findPartnerIdByMatchingParticipantAndChattingRoom(matchingParticipant, chattingRoom);

        //then: 대화 상대 id 반환 성공
        assertEquals(partnerId, 18);
    }
}