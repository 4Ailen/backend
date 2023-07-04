package com.aliens.friendship.matching.repository;

import com.aliens.friendship.domain.chatting.repository.ChattingRoomRepository;
import com.aliens.friendship.domain.matching.domain.Applicant;
import com.aliens.friendship.domain.matching.domain.Matching;
import com.aliens.friendship.domain.chatting.domain.ChattingRoom;
import com.aliens.friendship.domain.matching.domain.Language;
import com.aliens.friendship.domain.matching.repository.MatchingRepository;
import com.aliens.friendship.domain.matching.repository.ApplicantRepository;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.repository.MemberRepository;
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
class ServiceModelMatchingRepositoryImplTest {

    @Autowired
    MatchingRepository matchingRepository;

    @Autowired
    private ChattingRoomRepository chattingRoomRepository;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("특정 1:1 채팅방의 대화 상대방 id 반환 성공")
    void GetPartnerId_When_GivenMatchingParticipantAndChattingRoom() {
        // given: 신청자 4명(0, 1, 2, 3), 채팅룸[0](신청자[0], 신청자[1]), 채팅룸[1](신청자[2], 신청자[3])
        List<Member> members = createMember();
        List<Applicant> applicants = createMatchingParticipant(members);
        List<ChattingRoom> chattingRooms = createChattingRoom();
        createChatting(chattingRooms, applicants);

        // when: 매칭 신청자가 입장하는 1: 1 채팅방의 대화 상대 id 반환
        Integer partnerId1 = matchingRepository.findPartnerIdByApplicantAndChattingRoom(applicants.get(0), chattingRooms.get(0));
        Integer partnerId2 = matchingRepository.findPartnerIdByApplicantAndChattingRoom(applicants.get(2), chattingRooms.get(1));

        // then: 대화 상대 id 반환 성공
        assertEquals(partnerId1, applicants.get(1).getId());
        assertEquals(partnerId2, applicants.get(3).getId());
    }

    private List<Member> createMember() {
        List<Member> members = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Member member = Member.builder()
                    .email("member" + i + "@test.com")
                    .password("1234567")
                    .mbti(Member.Mbti.ISFJ)
                    .gender("FEMALE")
                    .birthday("2002-01-17")
                    .name("최정은")
                    .nationality("South Korea")
                    .joinDate(Instant.now())
                    .profileImageUrl("/testUrl")
                    .build();
            member.updateStatus(Member.Status.APPLIED);
            memberRepository.save(member);
            members.add(member);
        }

        return members;
    }

    private List<Applicant> createMatchingParticipant(List<Member> members) {
        List<Applicant> applicants = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Applicant applicant = Applicant.builder()
                    .member(members.get(i))
                    .isMatched(Applicant.Status.MATCHED)
                    .firstPreferLanguage(new Language(1, "Korean"))
                    .secondPreferLanguage(new Language(2, "English"))
                    .build();
            applicantRepository.save(applicant);
            applicants.add(applicant);
        }

        return applicants;
    }

    private List<ChattingRoom> createChattingRoom() {
        List<ChattingRoom> chattingRooms = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            chattingRooms.add(chattingRoomRepository.save(ChattingRoom.builder()
                    .id(Long.valueOf(i * (-1)))
                    .status(ChattingRoom.RoomStatus.OPEN).build()));
        }

        return chattingRooms;
    }

    private void createChatting(List<ChattingRoom> chattingRooms, List<Applicant> applicants) {
        matchingRepository.save(Matching.builder()
                .chattingRoom(chattingRooms.get(0))
                .applicant(applicants.get(0)).build());
        matchingRepository.save(Matching.builder()
                .chattingRoom(chattingRooms.get(0))
                .applicant(applicants.get(1)).build());
        matchingRepository.save(Matching.builder()
                .chattingRoom(chattingRooms.get(1))
                .applicant(applicants.get(2)).build());
        matchingRepository.save(Matching.builder()
                .chattingRoom(chattingRooms.get(1))
                .applicant(applicants.get(3)).build());
    }
}