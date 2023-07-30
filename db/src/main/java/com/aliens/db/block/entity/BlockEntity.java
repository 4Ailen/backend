package com.aliens.db.block.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@AllArgsConstructor @NoArgsConstructor
@Getter @ToString @Entity
@Table(name = "block", schema = "aliendb")
public class BlockEntity extends BaseEntity {

    @ManyToOne( optional = false, cascade=CascadeType.REMOVE)
    @JoinColumn(name = "blocked_member_id")
    private MemberEntity blockedMemberEntity;

    @ManyToOne( optional = false, cascade=CascadeType.REMOVE)
    @JoinColumn(name = "blocking_member_id")
    private MemberEntity blockingMemberEntity;

}