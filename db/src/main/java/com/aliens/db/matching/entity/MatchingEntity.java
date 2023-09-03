package com.aliens.db.matching.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.chatting.entity.ChattingRoomEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;

@NoArgsConstructor @AllArgsConstructor @SuperBuilder
@Entity @Getter
@Table(name ="matching", schema = "aliendb")
public class MatchingEntity extends BaseEntity {

    @ManyToOne(optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "maching_member_id", nullable = false)
    private MemberEntity matchingMember;

    @ManyToOne( optional = false)
    @JoinColumn(name = "matched_member_id", nullable = false)
    private MemberEntity matchedMember;

    @ManyToOne( optional = false)
    @JoinColumn(name = "chattingroom_Id", nullable = false)
    private ChattingRoomEntity chattingRoomEntity;

    private Instant matchingDate; //매칭 날짜와 생성 날짜는 다르다.

}