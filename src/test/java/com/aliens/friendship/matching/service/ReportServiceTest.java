package com.aliens.friendship.matching.service;

import com.aliens.friendship.matching.controller.dto.ReportRequest;
import com.aliens.friendship.matching.domain.Report;
import com.aliens.friendship.matching.domain.ReportCategory;
import com.aliens.friendship.matching.repository.ReportRepository;
import com.aliens.friendship.member.domain.Member;
import com.aliens.friendship.member.exception.MemberNotFoundException;
import com.aliens.friendship.member.repository.MemberRepository;
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
public class ReportServiceTest {

    @InjectMocks
    ReportService reportService;

    @Mock
    ReportRepository reportRepository;

    @Mock
    MemberRepository memberRepository;

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
    @DisplayName("신고 성공")
    void Report_Success() {
        //given
        ReportRequest reportRequest = ReportRequest.builder()
                .reportCategory(ReportCategory.VIOLENCE)
                .reportContent("폭력적인 언행")
                .build();
        Member reporterMember = MemberFixture.createTestMember();
        Member reportedMember = mock(Member.class);
        Integer reportedMemberId = 2;
        when(memberRepository.findByEmail(reporterMember.getEmail())).thenReturn(Optional.of(reporterMember));
        when(memberRepository.findById(reportedMemberId)).thenReturn(Optional.of(reportedMember));
        setUpAuthentication(reporterMember);

        //when
        reportService.report(reportedMemberId, reportRequest);

        // then
        verify(memberRepository, times(1)).findByEmail(reporterMember.getEmail());
        verify(memberRepository, times(1)).findById(reportedMemberId);
        ArgumentCaptor<Report> captor = ArgumentCaptor.forClass(Report.class);
        verify(reportRepository, times(1)).save(captor.capture());
        Report report = captor.getValue();
        assertEquals(report.getReportCategory(), reportRequest.getReportCategory());
        assertEquals(report.getReportContent(), reportRequest.getReportContent());
    }

    @Test
    @DisplayName("신고 예외 - 신고 대상이 존재하지 않는 회원일 경우")
    void Blocking_ThrowException_When_GivenNotExistMember() throws Exception {
        //given
        Integer NotExistMemberId = 2;
        Member blockingMember = MemberFixture.createTestMember();
        when(memberRepository.findById(NotExistMemberId)).thenReturn(Optional.empty());
        ReportRequest reportRequest = ReportRequest.builder()
                .reportCategory(ReportCategory.VIOLENCE)
                .reportContent("폭력적인 언행")
                .build();
        setUpAuthentication(blockingMember);

        // when
        Exception exception = assertThrows(MemberNotFoundException.class, () -> {
            reportService.report(NotExistMemberId, reportRequest);
        });

        // then
        verify(memberRepository, times(1)).findByEmail(blockingMember.getEmail());
        assertEquals("존재하지 않는 회원입니다.", exception.getMessage());
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