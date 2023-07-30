package com.aliens.friendship.domain.member.controller.dto;

import com.aliens.db.report.ReportCategory;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReportResponseDto {

    private List<Report> reports;

    public ReportResponseDto() {
        this.reports = new ArrayList<>();
    }

    @Getter
    @Builder
    public static class Report {
        private Long reportId;
        private Long reportedMemberId;
        private Long reporterMemberId;
        private ReportCategory reportCategory;
        private String reportContent;
        private String reportDate;
    }
}
