package com.aliens.friendship.domain.applicant.service;

import com.aliens.db.applicant.entity.ApplicantEntity;
import com.aliens.db.applicant.repository.ApplicantRepository;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.member.repository.MemberRepository;
import com.aliens.friendship.domain.applicant.controller.dto.ApplicantRequestDto;
import com.aliens.friendship.domain.match.exception.ApplicantNotFoundException;
import com.aliens.friendship.domain.match.exception.MatchNotFoundException;
import com.aliens.friendship.domain.match.exception.MatchRequestNotSubmitted;
import com.aliens.friendship.domain.match.exception.MatchingCompletedException;
import com.aliens.friendship.domain.member.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicantService {

    private final ApplicantRepository applicantRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void register(ApplicantEntity applicantEntity) {
        applicantRepository.save(applicantEntity);
    }

    public ApplicantEntity findByMemberEntity(MemberEntity memberEntity) {
        ApplicantEntity applicantEntity = applicantRepository
                .findFirstByMemberEntityOrderByCreatedAtDesc(memberEntity)
                .orElseThrow(MatchRequestNotSubmitted::new);
        return applicantEntity;
    }

    /**
     v1 기준 매칭날짜(금요일) 반환
     */
    public Instant getDateWillMatched(ApplicantEntity applicantEntity) {
        Instant applicationDateInstant = applicantEntity.getCreatedAt();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate applicationDate = applicationDateInstant.atZone(zoneId).toLocalDate();
        DayOfWeek dayOfWeek = applicationDate.getDayOfWeek();

        // applicationDate가 금요일 이전인 경우, 그 주의 금요일 00시를 반환
        if (dayOfWeek.compareTo(DayOfWeek.FRIDAY) < 0) {
            return getThisFriday(applicationDate);
        }
        // 금요일 이후인 경우, 다음 주의 금요일 00시를 반환
        else {
            LocalDate nextFriday = applicationDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
            return getNextFriday(nextFriday);
        }
    }


    /**
     v2 기준 매칭날짜(화요일, 금요일) 반환
     */
//    public Instant getDateWillMatched(ApplicantEntity applicantEntity) {
//        Instant applicationDateInstant = applicantEntity.getCreatedAt();
//        ZoneId zoneId = ZoneId.systemDefault();
//        LocalDate applicationDate = applicationDateInstant.atZone(zoneId).toLocalDate();
//        DayOfWeek dayOfWeek = applicationDate.getDayOfWeek();
//
//        // applicationDate가 화요일, 수요일, 목요일에 속하는 경우, 그 주의 금요일 00시를 반환
//        if (dayOfWeek == DayOfWeek.TUESDAY || dayOfWeek == DayOfWeek.WEDNESDAY || dayOfWeek == DayOfWeek.THURSDAY) {
//            ZonedDateTime nextFriday = applicationDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).atTime(LocalTime.MIN).atZone(ZoneId.systemDefault());
//            return nextFriday.toInstant();
//
//        }
//
//        //월요일인 경우 (내일) 화요일 00시 반환
//        else if (dayOfWeek == DayOfWeek.MONDAY) {
//            ZonedDateTime tomorrow = applicationDate.with(TemporalAdjusters.next(DayOfWeek.TUESDAY)).atTime(LocalTime.MIN).atZone(ZoneId.systemDefault());
//            return tomorrow.toInstant();
//        }
//
//        else { // 그 외 (금요일, 토요일, 일요일), 다음 주의 화요일 00시를 반환
//            ZonedDateTime nextTuesday = applicationDate.with(TemporalAdjusters.next(DayOfWeek.TUESDAY)).atTime(LocalTime.MIN).atZone(ZoneId.systemDefault());
//            return nextTuesday.toInstant();
//        }
//    }

    public List<ApplicantEntity> findAllParticipants() {
        List<ApplicantEntity> applicantEntities = applicantRepository.findAllByIsMatched(ApplicantEntity.Status.NOT_MATCHED);
        return applicantEntities;
    }

    public ApplicantEntity findById(Long id) {
        ApplicantEntity applicantEntity = applicantRepository.findById(id).orElseThrow(ApplicantNotFoundException::new);
        return applicantEntity;
    }

    @Transactional
    public void updateIsMatched(ApplicantEntity applicantEntity) {
        applicantEntity.updateIsMatched(ApplicantEntity.Status.MATCHED);
    }

    @Transactional
    public void changePreferLanguages(ApplicantRequestDto applicantRequestDto) throws Exception {
        MemberEntity memberEntity = getCurrentMemberEntity();
        ApplicantEntity applicantEntity = findByMemberEntity(memberEntity);

       if(!isMatched(applicantEntity)){
           applicantEntity.updatePreferLanguages(ApplicantEntity.Language.valueOf(applicantRequestDto.getFirstPreferLanguage()), ApplicantEntity.Language.valueOf(applicantRequestDto.getSecondPreferLanguage()));
       } else{
           throw new MatchingCompletedException();
       }
    }

    public static Instant getThisFriday(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)) // 이번 주 금요일 반환
                .atTime(LocalTime.MIN)
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    public static Instant getNextFriday(LocalDate date) {
        return date.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY)) // 다음 주 금요일 반환
                .atTime(LocalTime.MIN)
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    private boolean isApplied(MemberEntity memberEntity){
        return memberEntity.getStatus() == MemberEntity.Status.APPLIED;
    }

    private boolean isMatched(ApplicantEntity applicantEntity){
        return applicantEntity.getIsMatched() == ApplicantEntity.Status.MATCHED;
    }

    private MemberEntity getCurrentMemberEntity() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String MemberEmail = userDetails.getUsername();
        MemberEntity memberEntity = findByEmail(MemberEmail);
        return memberEntity;
    }

    private MemberEntity findByEmail(String email) throws Exception {
        return memberRepository.findByEmail(email).orElseThrow(MemberNotFoundException::new);
    }
}
