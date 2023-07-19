package com.aliens.friendship.domain.matching.controller.dto;

import com.aliens.friendship.domain.matching.domain.Report;
import com.aliens.friendship.domain.matching.domain.ReportCategory;
import com.aliens.friendship.domain.member.domain.Member;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ReportResponse {

    private List<Report> reports;

    public ReportResponse() {
        this.reports = new ArrayList<>();
    }

    @Getter
    @Builder
    public static class Report {
        private Integer reportId;
        private Integer reportedMemberId;
        private Integer reporterMemberId;
        private ReportCategory reportCategory;
        private String reportContent;
        private String reportDate;
    }
}
