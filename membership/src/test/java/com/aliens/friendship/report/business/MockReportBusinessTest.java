package com.aliens.friendship.report.business;


import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.report.entity.ReportEntity;
import com.aliens.friendship.domain.member.controller.dto.ReportRequestDto;
import com.aliens.friendship.domain.member.controller.dto.ReportResponseDto;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.domain.report.business.ReportBusiness;
import com.aliens.friendship.domain.report.converter.ReportConverter;
import com.aliens.friendship.domain.report.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MockReportBusinessTest {

    @Mock
    private ReportService reportService;

    @Mock
    private MemberService memberService;

    @Mock
    private ReportConverter reportConverter;

    @InjectMocks
    private ReportBusiness reportBusiness;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("신고하기 - 성공")
    public void testReport() throws Exception {
        //when
        when(memberService.getCurrentMemberEntity()).thenReturn(MemberEntity.builder().build());
        when(memberService.findById(anyLong())).thenReturn(MemberEntity.builder().build());
        when(reportConverter.toReportEntity(any(ReportRequestDto.class), any(MemberEntity.class), any(MemberEntity.class)))
                .thenReturn(new ReportEntity());

        // then
        reportBusiness.report(1L, new ReportRequestDto());

        verify(memberService, times(1)).getCurrentMemberEntity();
        verify(memberService, times(1)).findById(anyLong());
        verify(reportService, times(1)).register(any(ReportEntity.class));
    }

    @Test
    @DisplayName("관리자용 신고 내역 조회 - 성공")
    public void testGetReportsByAdmin() {
        //when
        when(reportService.findAll()).thenReturn(new ArrayList<>());
        when(reportConverter.toReportResponseDto(any(ReportEntity.class))).thenReturn( ReportResponseDto.Report.builder().build());

        //then
        ReportResponseDto resultDto = reportBusiness.getReportsByAdmin();

        verify(reportService, times(1)).findAll();
        assertEquals(0, resultDto.getReports().size());
    }


}