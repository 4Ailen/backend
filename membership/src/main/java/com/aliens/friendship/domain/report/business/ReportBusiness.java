package com.aliens.friendship.domain.report.business;

import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.report.entity.ReportEntity;
import com.aliens.friendship.domain.member.controller.dto.ReportRequestDto;
import com.aliens.friendship.domain.member.controller.dto.ReportResponseDto;
import com.aliens.friendship.domain.member.service.MemberService;
import com.aliens.friendship.domain.report.converter.ReportConverter;
import com.aliens.friendship.domain.report.service.ReportService;
import com.aliens.friendship.global.common.annotation.Business;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@RequiredArgsConstructor
@Business
@Slf4j
public class ReportBusiness {
    private final ReportService reportService;
    private final MemberService memberService;
    private final ReportConverter reportConverter;

    /**
     * 회원간 신고
     */
    public void report(Long reportedMemberId, ReportRequestDto reportRequestDto) throws Exception {

        // 로그인 회원 엔티티
        MemberEntity loginMemberEntity  = memberService.getCurrentMemberEntity();

        // 신고할 회원 엔티티
        MemberEntity reportedMemberEntity = memberService.findById(reportedMemberId);

        // ReportEntity 변환
        ReportEntity reportEntity = reportConverter.toReportEntity(reportRequestDto,loginMemberEntity,reportedMemberEntity);

        //신고 저장
        reportService.register(reportEntity);

    }


    /**
     * 관리자용 신고 내역 조회
     */
    public ReportResponseDto getReportsByAdmin() {
        // 신고 전체 내역 조회
        List<ReportEntity> reportEntities  = reportService.findAll();

        // 결과 Dto
        ReportResponseDto resultDto  = new ReportResponseDto();

        //entity -> Dto
        for(ReportEntity reportEntity : reportEntities){
            ReportResponseDto.Report reportDto = reportConverter.toReportResponseDto(reportEntity);
            resultDto.getReports().add(reportDto);
        }

        return resultDto;
    }

}
