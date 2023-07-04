package com.aliens.friendship.chatting.service;

import com.aliens.friendship.domain.auth.dto.RoomInfoDto;
import com.aliens.friendship.domain.chatting.domain.ChattingRoom;
import com.aliens.friendship.domain.chatting.repository.ChattingRoomRepository;
import com.aliens.friendship.domain.chatting.service.ChattingJwtTokenUtil;
import com.aliens.friendship.domain.chatting.service.ChattingService;
import com.aliens.friendship.domain.matching.domain.Applicant;
import com.aliens.friendship.domain.matching.domain.Language;
import com.aliens.friendship.domain.matching.domain.Matching;
import com.aliens.friendship.domain.matching.repository.ApplicantRepository;
import com.aliens.friendship.domain.matching.repository.MatchingRepository;
import com.aliens.friendship.domain.member.controller.dto.JoinDto;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.domain.Nationality;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.global.config.security.CustomUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
class ChattingServiceTest {

    @Mock
    ChattingJwtTokenUtil chattingJwtTokenUtil;

    @Mock
    ChattingRoomRepository chattingRoomRepository;

    @Mock
    MemberService memberService;

    @Mock
    MemberRepository memberRepository;

    @Mock
    ApplicantRepository applicantRepository;

    @Mock
    MatchingRepository matchingRepository;

    @Spy
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @InjectMocks
    ChattingService chattingService;


    @Test
    @DisplayName("채팅 토큰발급 성공")
    void getJWTTokenForChatting_Success() {
        //given: 매칭과 로그인 멤버 정보
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member spyMember = createSpyMember(mockJoinDto);
        List<Long> roomIds = Arrays.asList(1L,2L,3L);
        Applicant applicant = Applicant.builder().
                id(1)
                .member(spyMember)
                .firstPreferLanguage(Language.builder().languageText("korean").id(1).build())
                .secondPreferLanguage(Language.builder().languageText("English").id(2).build())
                .isMatched(Applicant.Status.MATCHED).build();

        ChattingRoom chattingRoom = ChattingRoom.builder().status(ChattingRoom.RoomStatus.OPEN).id(1L).build();
        Matching matching1 = Matching.builder()
                .id(1)
                .applicant(applicant)
                .chattingRoom(chattingRoom)
                .build();
        chattingRoom = ChattingRoom.builder().status(ChattingRoom.RoomStatus.OPEN).id(2L).build();
        Matching matching2 = Matching.builder()
                .id(2)
                .applicant(applicant)
                .chattingRoom(chattingRoom)
                .build();
        chattingRoom = ChattingRoom.builder().status(ChattingRoom.RoomStatus.OPEN).id(3L).build();
        Matching matching3 = Matching.builder()
                .id(3)
                .applicant(applicant)
                .chattingRoom(chattingRoom)
                .build();
        List<Matching> matchings = Arrays.asList(matching1,matching2,matching3);
        when(Optional.of(spyMember).get().getId()).thenReturn(1);
        when(applicantRepository.findById(1)).thenReturn(Optional.of(applicant));
        when(memberRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(Optional.of(spyMember));
        when(matchingRepository.findByApplicant(applicant)).thenReturn(matchings);
        when(chattingJwtTokenUtil.generateToken(spyMember.getId(),roomIds)).thenReturn("testToken");
        setAuthenticationWithSpyMember(spyMember);

        //when: 채팅JWT 토큰 발급 요청
        String token = chattingService.getJWTTokenForChatting();

        //then:채팅JWT 토큰 발급 성공
        verify(memberRepository, times(1)).findByEmail(anyString());
        verify(applicantRepository,times(1)). findById(anyInt());
        verify(matchingRepository,times(1)). findByApplicant(applicant);
        assertEquals("testToken",token);
    }

    @Test
    @DisplayName("매칭된 채티방 정보 목록 요청 성공")
    void getRoomInfoDtoListByMatchingCurrentMemberId_Success() {
        //given: 매칭과 로그인 멤버 정보
        JoinDto mockJoinDto = createMockJoinDto("test@case.com", "TestPassword");
        Member spyMember = createSpyMember(mockJoinDto);
        List<Long> roomIds = Arrays.asList(1L,2L,3L);
        Applicant applicant = Applicant.builder().
                id(1)
                .member(spyMember)
                .firstPreferLanguage(Language.builder().languageText("korean").id(1).build())
                .secondPreferLanguage(Language.builder().languageText("English").id(2).build())
                .isMatched(Applicant.Status.MATCHED).build();

        ChattingRoom chattingRoom1 = ChattingRoom.builder().status(ChattingRoom.RoomStatus.OPEN).id(1L).build();
        Matching matching1 = Matching.builder()
                .id(1)
                .applicant(applicant)
                .chattingRoom(chattingRoom1)
                .build();
        ChattingRoom chattingRoom2 = ChattingRoom.builder().status(ChattingRoom.RoomStatus.OPEN).id(2L).build();
        Matching matching2 = Matching.builder()
                .id(2)
                .applicant(applicant)
                .chattingRoom(chattingRoom2)
                .build();
        ChattingRoom chattingRoom3 = ChattingRoom.builder().status(ChattingRoom.RoomStatus.OPEN).id(3L).build();
        Matching matching3 = Matching.builder()
                .id(3)
                .applicant(applicant)
                .chattingRoom(chattingRoom3)
                .build();
        List<Matching> matchings = Arrays.asList(matching1,matching2,matching3);
        when(Optional.of(spyMember).get().getId()).thenReturn(1);
        when(applicantRepository.findById(1)).thenReturn(Optional.of(applicant));
        when(memberRepository.findByEmail(mockJoinDto.getEmail())).thenReturn(Optional.of(spyMember));
        when(matchingRepository.findByApplicant(applicant)).thenReturn(matchings);
        when(matchingRepository.findPartnerIdByApplicantAndChattingRoom(applicant, chattingRoom1)).thenReturn(1);
        when(matchingRepository.findPartnerIdByApplicantAndChattingRoom(applicant, chattingRoom2)).thenReturn(2);
        when(matchingRepository.findPartnerIdByApplicantAndChattingRoom(applicant, chattingRoom3)).thenReturn(3);

        setAuthenticationWithSpyMember(spyMember);

        //when: 매칭된 채티방 정보 목록 조회 요청
        List<RoomInfoDto> roomInfoDtos = chattingService.getRoomInfoDtoListByMatchingCurrentMemberId();

        //then: 매칭된 채티방 정보 목록 조회 성공
        verify(memberRepository, times(1)).findByEmail(anyString());
        verify(applicantRepository,times(1)). findById(anyInt());
        verify(matchingRepository,times(1)). findByApplicant(applicant);
        assertEquals(1,roomInfoDtos.get(0).getRoomId());
        assertEquals(2,roomInfoDtos.get(1).getRoomId());
        assertEquals(3,roomInfoDtos.get(2).getRoomId());

    }


    private JoinDto createMockJoinDto(String email, String password) {
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        return JoinDto.builder()
                .email(email)
                .password(password)
                .name("Ryan")
                .mbti(Member.Mbti.ENFJ)
                .gender("MALE")
                .nationality(new Nationality(1, "South Korea"))
                .birthday("1998-12-31")
                .profileImage(mockMultipartFile)
                .build();
    }

    private Member createSpyMember(JoinDto joinDto) {
        joinDto.setPassword(passwordEncoder.encode(joinDto.getPassword()));
        Member member = Member.ofUser(joinDto);
        Member spyMember = spy(member);
        return spyMember;
    }

    private void setAuthenticationWithSpyMember(Member mockMember) {
        UserDetails userDetails = CustomUserDetails.of(mockMember);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}