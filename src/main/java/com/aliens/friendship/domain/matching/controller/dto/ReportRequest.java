package com.aliens.friendship.domain.matching.controller.dto;

import com.aliens.friendship.domain.matching.domain.ReportCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportRequest {
    private ReportCategory reportCategory;
    private String reportContent;
}
