package com.aliens.friendship.domain.applicant.service;

import com.aliens.db.applicant.entity.ApplicantEntity;
import com.aliens.db.applicant.repository.ApplicantRepository;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.friendship.domain.match.exception.ApplicantNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicantService {

    private final ApplicantRepository applicantRepository;

    @Transactional
    public void register(ApplicantEntity applicantEntity) {
        applicantRepository.save(applicantEntity);
    }

    public ApplicantEntity getApplicantByMemberEntity(MemberEntity memberEntity) {
        ApplicantEntity applicantEntity = applicantRepository
                .findFirstByMemberEntityOrderByCreatedAtDesc(memberEntity)
                .orElseThrow(ApplicantNotFoundException::new);
        return applicantEntity;
    }

    public Optional<ApplicantEntity> findApplicantByMemberEntity(MemberEntity memberEntity) {
        Optional<ApplicantEntity> applicantEntity = applicantRepository
                .findFirstByMemberEntityOrderByCreatedAtDesc(memberEntity);
        return applicantEntity;
    }

    public Instant getDateWillMatched(ApplicantEntity applicantEntity) {
        Instant applicationDateInstant = applicantEntity.getCreatedAt();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate applicationDate = applicationDateInstant.atZone(zoneId).toLocalDate();
        DayOfWeek dayOfWeek = applicationDate.getDayOfWeek();

        // applicationDate가 화요일, 수요일, 목요일에 속하는 경우, 그 주의 금요일 00시를 반환
        if (dayOfWeek == DayOfWeek.TUESDAY || dayOfWeek == DayOfWeek.WEDNESDAY || dayOfWeek == DayOfWeek.THURSDAY) {
            ZonedDateTime nextFriday = applicationDate.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)).atTime(LocalTime.MIN).atZone(ZoneId.systemDefault());
            return nextFriday.toInstant();

        }

        //월요일인 경우 (내일) 화요일 00시 반환
        else if (dayOfWeek == DayOfWeek.MONDAY) {
            ZonedDateTime tomorrow = applicationDate.with(TemporalAdjusters.next(DayOfWeek.TUESDAY)).atTime(LocalTime.MIN).atZone(ZoneId.systemDefault());
            return tomorrow.toInstant();
        }

        else { // 그 외 (금요일, 토요일, 일요일), 다음 주의 화요일 00시를 반환
            ZonedDateTime nextTuesday = applicationDate.with(TemporalAdjusters.next(DayOfWeek.TUESDAY)).atTime(LocalTime.MIN).atZone(ZoneId.systemDefault());
            return nextTuesday.toInstant();
        }
    }

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
}
