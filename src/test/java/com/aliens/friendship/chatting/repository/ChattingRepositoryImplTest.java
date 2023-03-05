package com.aliens.friendship.chatting.repository;

import com.aliens.friendship.chatting.domain.Chatting;
import com.aliens.friendship.chatting.domain.ChattingRoom;
import com.aliens.friendship.matching.domain.Language;
import com.aliens.friendship.matching.domain.MatchingParticipant;
import com.aliens.friendship.matching.repository.MatchingParticipantRepository;
import com.aliens.friendship.member.domain.Member;
import com.aliens.friendship.member.domain.Nationality;
import com.aliens.friendship.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ChattingRepositoryImplTest {

    @Autowired
    ChattingRepository chattingRepository;

    @Autowired
    private ChattingRoomRepository chattingRoomRepository;

    @Autowired
    private MatchingParticipantRepository matchingParticipantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("특정 1:1 채팅방의 대화 상대방 id 반환 성공")
    void GetPartnerId_When_GivenMatchingParticipantAndChattingRoom() {
        // given: 신청자 4명(0, 1, 2, 3), 채팅룸[0](신청자[0], 신청자[1]), 채팅룸[1](신청자[2], 신청자[3])
        List<Member> members = createMember();
        List<MatchingParticipant> matchingParticipants = createMatchingParticipant(members);
        List<ChattingRoom> chattingRooms = createChattingRoom();
        createChatting(chattingRooms, matchingParticipants);

        // when: 매칭 신청자가 입장하는 1: 1 채팅방의 대화 상대 id 반환
        Integer partnerId1 = chattingRepository.findPartnerIdByMatchingParticipantAndChattingRoom(matchingParticipants.get(0), chattingRooms.get(0));
        Integer partnerId2 = chattingRepository.findPartnerIdByMatchingParticipantAndChattingRoom(matchingParticipants.get(2), chattingRooms.get(1));

        // then: 대화 상대 id 반환 성공
        assertEquals(partnerId1, matchingParticipants.get(1).getId());
        assertEquals(partnerId2, matchingParticipants.get(3).getId());
    }

    private List<Member> createMember() {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Member member = Member.builder()
                    .email("member" + i + "@test.com")
                    .password("1234567")
                    .mbti("ISFJ")
                    .gender("FEMALE")
                    .birthday("2002-01-17")
                    .name("최정은")
                    .nationality(new Nationality(1, "South Korea"))
                    .joinDate(Instant.now())
                    .imageUrl("/testUrl")
                    .build();
            member.updateIsApplied(Member.Status.APPLIED);
            memberRepository.save(member);
            members.add(member);
        }

        return members;
    }

    private List<MatchingParticipant> createMatchingParticipant(List<Member> members) {
        List<MatchingParticipant> matchingParticipants = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            MatchingParticipant matchingParticipant = MatchingParticipant.builder()
                    .member(members.get(i))
                    .isMatched(MatchingParticipant.Status.MATCHED)
                    .firstPreferLanguage(new Language(1, "Korean"))
                    .secondPreferLanguage(new Language(2, "English"))
                    .build();
            matchingParticipantRepository.save(matchingParticipant);
            matchingParticipants.add(matchingParticipant);
        }

        return matchingParticipants;
    }

    private List<ChattingRoom> createChattingRoom() {
        List<ChattingRoom> chattingRooms = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            chattingRooms.add(chattingRoomRepository.save(ChattingRoom.builder()
                    .id(i * (-1))
                    .status(ChattingRoom.RoomStatus.OPEN).build()));
        }

        return chattingRooms;
    }

    private void createChatting(List<ChattingRoom> chattingRooms, List<MatchingParticipant> matchingParticipants) {
        chattingRepository.save(Chatting.builder()
                .chattingRoom(chattingRooms.get(0))
                .matchingParticipant(matchingParticipants.get(0)).build());
        chattingRepository.save(Chatting.builder()
                .chattingRoom(chattingRooms.get(0))
                .matchingParticipant(matchingParticipants.get(1)).build());
        chattingRepository.save(Chatting.builder()
                .chattingRoom(chattingRooms.get(1))
                .matchingParticipant(matchingParticipants.get(2)).build());
        chattingRepository.save(Chatting.builder()
                .chattingRoom(chattingRooms.get(1))
                .matchingParticipant(matchingParticipants.get(3)).build());
    }
}