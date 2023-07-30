package com.aliens.db.applicant.repository;

import com.aliens.db.applicant.entity.ApplicantEntity;
import com.aliens.db.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicantRepository extends JpaRepository<ApplicantEntity, Long> {
    List<ApplicantEntity> findAllByIsMatched(ApplicantEntity.Status isMatched);
    Optional<ApplicantEntity> findFirstByMemberEntityOrderByCreatedAtDesc(MemberEntity memberEntity);
}