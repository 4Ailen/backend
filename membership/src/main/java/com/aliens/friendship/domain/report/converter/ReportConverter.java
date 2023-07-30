package com.aliens.friendship.domain.report.converter;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.report.entity.ReportEntity;
import com.aliens.friendship.domain.member.controller.dto.ReportRequestDto;
import com.aliens.friendship.domain.member.controller.dto.ReportResponseDto;
import com.aliens.friendship.global.common.annotation.Converter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Converter
public class ReportConverter {
    public ReportEntity toReportEntity(ReportRequestDto reportRequestDto, MemberEntity loginMemberEntity, MemberEntity reportedMemberEntity) {
        ReportEntity reportEntity = ReportEntity.builder()
                .reportedMemberEntity(reportedMemberEntity)
                .reportingMemberEntity(loginMemberEntity)
                .category(reportRequestDto.getReportCategory())
                .content(reportRequestDto.getReportContent())
                .build();
        return reportEntity;
    }

    public ReportResponseDto.Report toReportResponseDto(ReportEntity reportEntity) {
        ReportResponseDto.Report reportDto = ReportResponseDto.Report.builder()
                .reportId(reportEntity.getId())
                .reportedMemberId(reportEntity.getReportedMemberEntity().getId())
                .reporterMemberId(reportEntity.getReportingMemberEntity().getId())
                .reportCategory(reportEntity.getCategory())
                .reportContent(reportEntity.getContent())
                .reportDate(reportEntity.getCreatedAt().toString())
                .build();
        return reportDto;
    }
}
