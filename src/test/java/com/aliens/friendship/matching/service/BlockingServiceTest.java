package com.aliens.friendship.matching.service;

import com.aliens.friendship.domain.emailAuthentication.repository.EmailAuthenticationRepository;
import com.aliens.friendship.domain.matching.domain.BlockingInfo;
import com.aliens.friendship.domain.matching.repository.BlockingInfoRepository;
import com.aliens.friendship.domain.matching.service.BlockingInfoService;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.exception.MemberNotFoundException;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import com.aliens.friendship.domain.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlockingServiceTest {

    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberService memberService;

    @Mock
    BlockingInfoRepository blockingInfoRepository;

    @InjectMocks
    BlockingInfoService blockingInfoService;

    @Mock
    EmailAuthenticationRepository emailAuthenticationRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("차단 성공")
    void Blocking_Success_When_ValidMember() throws Exception {

        //given
        Integer id = 1;
        Member blockingMember = MemberFixture.createTestMember();
        Member blockedMember = mock(Member.class);
        when(blockedMember.getId()).thenReturn(id);
        when(memberRepository.findByEmail(blockingMember.getEmail())).thenReturn(Optional.of(blockingMember));
        when(memberRepository.findById(id)).thenReturn(Optional.of(blockedMember));
        setUpAuthentication(blockingMember);

        // when
        blockingInfoService.block(id);

        // then
        verify(memberRepository, times(1)).findByEmail(blockingMember.getEmail());
        verify(memberRepository, times(1)).findById(blockedMember.getId());
        ArgumentCaptor<BlockingInfo> captor = ArgumentCaptor.forClass(BlockingInfo.class);
        verify(blockingInfoRepository, times(1)).save(captor.capture());
        BlockingInfo blockingInfo = captor.getValue();
        assertEquals(blockingInfo.getBlockedMember(), blockedMember);
        assertEquals(blockingInfo.getBlockingMember(), blockingMember);
    }

    @Test
    @DisplayName("차단 예외: 상대가 존재하지 않는 회원일 경우")
    void Blocking_ThrowException_When_GivenNotExistMember() throws Exception {
        //given
        Integer id = 1;
        Member blockingMember = MemberFixture.createTestMember();
        when(memberRepository.findById(1)).thenReturn(Optional.empty());
        setUpAuthentication(blockingMember);

        // when
        Exception exception = assertThrows(MemberNotFoundException.class, () -> {
            blockingInfoService.block(id);
        });

        // then
        verify(memberRepository, times(1)).findByEmail(blockingMember.getEmail());
        assertEquals(exception.getMessage(), "존재하지 않는 회원입니다.");
    }

    private void setUpAuthentication(Member member) {
        Authentication auth = new UsernamePasswordAuthenticationToken("testuser", "password");
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        when(SecurityContextHolder.getContext().getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(member.getEmail());
        when(memberRepository.findByEmail(member.getEmail())).thenReturn(Optional.of(member));
    }
}