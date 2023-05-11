package com.aliens.friendship.domain.matching.service;

import com.aliens.friendship.domain.matching.controller.dto.ReportRequest;
import com.aliens.friendship.domain.matching.domain.Report;
import com.aliens.friendship.domain.matching.repository.ReportRepository;
import com.aliens.friendship.domain.member.domain.Member;
import com.aliens.friendship.domain.member.exception.MemberNotFoundException;
import com.aliens.friendship.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void report(int reportedMemberId, ReportRequest reportRequest) {
        Member reporterMember =  memberRepository.findByEmail(getCurrentMemberEmail()).orElseThrow(MemberNotFoundException::new);
        Member reportedMember = memberRepository.findById(reportedMemberId).orElseThrow(MemberNotFoundException::new);
        Report report = Report.builder()
                .reportedMember(reportedMember)
                .reporterMember(reporterMember)
                .reportCategory(reportRequest.getReportCategory())
                .reportContent(reportRequest.getReportContent())
                .build();
        reportRepository.save(report);
    }

    private String getCurrentMemberEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}