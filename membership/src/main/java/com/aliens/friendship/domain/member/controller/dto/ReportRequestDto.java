package com.aliens.friendship.domain.member.controller.dto;

import com.aliens.db.report.ReportCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequestDto {
    private ReportCategory reportCategory;
    private String reportContent;
}
