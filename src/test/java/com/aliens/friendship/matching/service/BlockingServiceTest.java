package com.aliens.friendship.matching.service;

import com.aliens.friendship.matching.domain.BlockingInfo;
import com.aliens.friendship.matching.repository.BlockingInfoRepository;
import com.aliens.friendship.matching.service.BlockingInfoService;
import com.aliens.friendship.member.controller.dto.JoinDto;
import com.aliens.friendship.member.domain.Member;
import com.aliens.friendship.member.domain.Nationality;
import com.aliens.friendship.member.repository.MemberRepository;
import com.aliens.friendship.member.repository.NationalityRepository;
import com.aliens.friendship.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class BlockingServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    @Autowired
    BlockingInfoRepository blockingInfoRepository;

    @Autowired
    BlockingInfoService blockingInfoService;

    @Autowired
    NationalityRepository nationalityRepository;


    @Test
    @DisplayName("차단 성공")
    void Blocking_Success_When_ValidMember() throws Exception {

        //given: 차단자와 피차단자 세팅
        JoinDto mockJoinDto1 = createMockJoinDto("idontwannameetyouagain@case.com", "TestPassword");
        JoinDto mockJoinDto2 = createMockJoinDto("plzGiveMeOneMoreTime@case.com", "TestPsword");
        memberService.join(mockJoinDto1);
        memberService.join(mockJoinDto2);
        Member blockedMember = memberRepository.findByEmail(mockJoinDto2.getEmail()).orElseThrow();

        //when: 차단 실행
        blockingInfoService.block(mockJoinDto1.getEmail(), blockedMember.getId());

        //then: 차단 목록 조회 성공
        List<BlockingInfo> blockingInfos = blockingInfoService.findAllByBlockingMember(mockJoinDto1.getEmail());
        assertThat(blockingInfos.get(0).getBlockedMember().getEmail()).isEqualTo(mockJoinDto2.getEmail());
        assertThat(blockingInfos.get(0).getBlockingMember().getEmail()).isEqualTo(mockJoinDto1.getEmail());
    }

    @Test
    @DisplayName("차단 예외: 상대가 존재하지 않는 회원일 경우")
    void Blocking_ThrowException_When_GivenNotExistMember() throws Exception {

        //given: 차단자와 피차단자 세팅
        JoinDto mockJoinDto1 = createMockJoinDto("idontwannameetyouagain@case.com", "TestPassword");
        JoinDto mockJoinDto2 = createMockJoinDto("plzGiveMeOneMoreTime@case.com", "TestPsword");
        memberService.join(mockJoinDto1);
        memberService.join(mockJoinDto2);
        Member blockedMember = memberRepository.findByEmail(mockJoinDto2.getEmail()).orElseThrow();
        int fakeMemberId = 1234;

        //when: 차단 실행
        Exception exception = assertThrows(Exception.class, () -> {
            blockingInfoService.block(mockJoinDto1.getEmail(), fakeMemberId);
        });


        //then: 예외 발생
        assertEquals("존재하지 않는 회원입니다.", exception.getMessage());
    }


    private JoinDto createMockJoinDto(String email, String password) {
        MultipartFile mockMultipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test data".getBytes());
        Nationality nationality = new Nationality(1, "South Korea");
        nationalityRepository.save(nationality);
        return JoinDto.builder()
                .email(email)
                .password(password)
                .name("Ryan")
                .mbti("ENFJ")
                .gender("MALE")
                .nationality(nationality)
                .birthday("1998-12-31")
                .profileImage(mockMultipartFile)
                .build();
    }
}