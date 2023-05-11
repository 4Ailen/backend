package com.aliens.friendship.domain.matching.controller.dto;

import com.aliens.friendship.domain.matching.domain.ReportCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportRequest {
    private ReportCategory reportCategory;
    private String reportContent;
}
