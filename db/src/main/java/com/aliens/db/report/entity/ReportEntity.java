package com.aliens.db.report.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.member.entity.MemberEntity;
import com.aliens.db.report.ReportCategory;
import com.sun.istack.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import javax.validation.constraints.Size;

@SuperBuilder
@AllArgsConstructor @NoArgsConstructor
@Getter @ToString @Entity
@Table(name = "report", schema = "aliendb")
public class ReportEntity extends BaseEntity {

    @NonNull
    @ManyToOne(optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "reported_member_id", nullable = false)
    private MemberEntity reportedMemberEntity;

    @NotNull
    @ManyToOne(optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "reporting_member_id", nullable = false)
    private MemberEntity reportingMemberEntity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column( nullable = false)
    private ReportCategory category;

    @Size(max = 255)
    @NotNull
    @Column( nullable = false)
    private String content;

}