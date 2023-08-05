package com.aliens.db.matching.repository;

import com.aliens.db.chatting.entity.ChattingRoomEntity;
import com.aliens.db.matching.entity.MatchingEntity;
import com.aliens.db.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<MatchingEntity, Long> {

    Optional<MatchingEntity> findByMatchingMemberAndMatchedMemberAndMatchingDate(MemberEntity matchingMember, MemberEntity matchedMember, Instant matchingDate);

    List<MatchingEntity> findAllByCreatedAtBetweenAndMatchingMember(Instant start, Instant end, MemberEntity matchingMemberEntity);

    @Query("SELECT m FROM MatchingEntity m JOIN FETCH m.chattingRoomEntity c " +
            "WHERE m.matchingMember = :member AND c.status = :status")
    List<MatchingEntity> findAllByMatchingMemberAndChattingRoomEntityStatus(
            @Param("member") MemberEntity member,
            @Param("status") ChattingRoomEntity.RoomStatus status
    );

    List<MatchingEntity> findAllByMatchingMember(MemberEntity matchingMember);
}