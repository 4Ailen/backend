package com.aliens.friendship.matching.controller.dto;

import com.aliens.friendship.matching.domain.ReportCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportRequest {
    private ReportCategory reportCategory;
    private String reportContent;
}
