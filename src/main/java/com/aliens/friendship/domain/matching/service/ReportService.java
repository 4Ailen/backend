package com.aliens.friendship.domain.matching.service;

import com.aliens.friendship.domain.matching.controller.dto.PartnersResponse;
import com.aliens.friendship.domain.matching.controller.dto.ReportRequest;
import com.aliens.friendship.domain.matching.controller.dto.ReportResponse;
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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


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
                .reportDate(getCurrentKoreanDateAsString())
                .build();
        reportRepository.save(report);
    }

    private String getCurrentMemberEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    public ReportResponse getReportsByAdmin() {
        List<Report> reports = reportRepository.findAll();
        ReportResponse reportResponse = new ReportResponse();
        for(Report report : reports){
            ReportResponse.Report reportDto = ReportResponse.Report.builder()
                    .reportId(report.getId())
                    .reportedMemberId(report.getReportedMember().getId())
                    .reporterMemberId(report.getReporterMember().getId())
                    .reportCategory(report.getReportCategory())
                    .reportContent(report.getReportContent())
                    .reportDate(report.getReportDate())
                    .build();
            reportResponse.getReports().add(reportDto);
        }

        return reportResponse;
    }

    private String getCurrentKoreanDateAsString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        return sdf.format(new Date());
    }
}