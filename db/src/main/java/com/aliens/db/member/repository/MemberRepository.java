package com.aliens.db.member.repository;

import com.aliens.db.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByEmail(String email);
    Optional<MemberEntity> findById(Long memberId);
    boolean existsByEmail(String email);
    Optional<MemberEntity> findByEmailAndName(String email, String name);

    List<MemberEntity> findAllByStatus(MemberEntity.Status applied);
}