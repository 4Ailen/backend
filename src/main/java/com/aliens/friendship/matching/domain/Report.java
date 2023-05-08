package com.aliens.friendship.matching.domain;

import com.aliens.friendship.member.domain.Member;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static javax.persistence.GenerationType.IDENTITY;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = Report.TABLE_NAME, schema = "aliendb")
public class Report {

    public static final String TABLE_NAME = "report";
    public static final String COLUMN_ID_NAME = "report_id";
    public static final String COLUMN_REPORTEDMEMBER_NAME = "reported_member_id";
    public static final String COLUMN_REPORTERMEMBER_NAME = "reporter_member_id";
    public static final String COLUMN_REPORTCATEGORY_NAME = "report_category";
    public static final String COLUMN_REPORTCONTENT_NAME = "report_content";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_REPORTEDMEMBER_NAME, nullable = false)
    private Member reportedMember;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_REPORTERMEMBER_NAME, nullable = false)
    private Member reporterMember;

    @Size(max = 15)
    @NotNull
    @Column(name = COLUMN_REPORTCATEGORY_NAME, nullable = false)
    private ReportCategory reportCategory;

    @Size(max = 255)
    @NotNull
    @Column(name = COLUMN_REPORTCONTENT_NAME, nullable = false)
    private String reportContent;
}