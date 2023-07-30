package com.aliens.friendship.domain.applicant.converter;

import com.aliens.db.applicant.entity.ApplicantEntity;
import com.aliens.db.matching.entity.MatchingEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.applicant.controller.dto.ApplicantRequestDto;
import com.aliens.friendship.domain.applicant.controller.dto.ApplicantResponseDto;
import com.aliens.friendship.domain.applicant.controller.dto.PartnersResponseDto;
import com.aliens.friendship.global.common.annotation.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Converter
public class ApplicantConverter {

    @Value("${file-server.domain}")
    private String domainUrl;

    public ApplicantResponseDto toApplicantResponseDto(ApplicantEntity applicantEntity) throws Exception {
        ApplicantResponseDto.Member applicantDto = ApplicantResponseDto.Member.builder()
                .name(applicantEntity.getMemberEntity().getName())
                .gender(applicantEntity.getMemberEntity().getGender())
                .mbti(applicantEntity.getMemberEntity().getMbti())
                .age(applicantEntity.getMemberEntity().getAge())
                .nationality(applicantEntity.getMemberEntity().getNationality())
                .profileImage(applicantEntity.getMemberEntity().getProfileImageUrl())
                .countryImage(applicantEntity.getMemberEntity().getNationality())
                .build();
        ApplicantResponseDto.PreferLanguages preferLanguagesDto = ApplicantResponseDto.PreferLanguages.builder()
                .firstPreferLanguage(applicantEntity.getFirstPreferLanguage().toString())
                .secondPreferLanguage(applicantEntity.getSecondPreferLanguage().toString())
                .build();
        return ApplicantResponseDto.builder()
                .member(applicantDto)
                .preferLanguages(preferLanguagesDto)
                .build();
    }

    public ApplicantEntity toApplicantEntity(MemberEntity loginMemberEntity, ApplicantRequestDto applicantRequestDto) {
        ApplicantEntity applicantEntity = ApplicantEntity.builder()
                .memberEntity(loginMemberEntity)
                .firstPreferLanguage(ApplicantEntity.Language.valueOf(applicantRequestDto.getFirstPreferLanguage()))
                .secondPreferLanguage(ApplicantEntity.Language.valueOf(applicantRequestDto.getSecondPreferLanguage()))
                .isMatched(ApplicantEntity.Status.NOT_MATCHED)
                .build();
        return applicantEntity;
    }

    public PartnersResponseDto.Partner getWithdrawalMember() {
        PartnersResponseDto.Partner partnerDto = PartnersResponseDto.Partner.builder()
                .memberId((long) -1)
                .name("탈퇴한 회원")
                .build();
        return partnerDto;
    }


    public PartnersResponseDto.Partner toPartnerOfResponseDto(MatchingEntity matching, ApplicantEntity applicantEntity) {
        PartnersResponseDto.Partner partnerDto = PartnersResponseDto.Partner.builder()
                .roomState(matching.getChattingRoomEntity().getStatus().toString())
                .roomId(matching.getChattingRoomEntity().getId())
                .name(matching.getMatchedMember().getName())
                .nationality(matching.getMatchedMember().getNationality())
                .gender(matching.getMatchedMember().getGender())
                .mbti(matching.getMatchedMember().getMbti())
                .memberId(matching.getMatchedMember().getId())
                .profileImage(matching.getMatchedMember().getProfileImageUrl())
                .firstPreferLanguage(applicantEntity.getFirstPreferLanguage().toString())
                .secondPreferLanguage(applicantEntity.getSecondPreferLanguage().toString())
                .build();
        return partnerDto;
    }

    public String getFormattedStringDate(Instant matchedDate) {
        ZonedDateTime zonedDateTime = matchedDate.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateString = formatter.format(zonedDateTime);
        return dateString;
    }
}
