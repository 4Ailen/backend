package com.aliens.friendship.domain.matching.domain;

import com.aliens.friendship.domain.member.domain.Member;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.Instant;

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
    public static final String COLUMN_REPORTDATE_NAME = "report_date";

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = COLUMN_REPORTEDMEMBER_NAME, nullable = false)
    private Member reportedMember;

    @NotNull
    @ManyToOne(optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = COLUMN_REPORTERMEMBER_NAME, nullable = false)
    private Member reporterMember;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_REPORTCATEGORY_NAME, nullable = false)
    private ReportCategory reportCategory;

    @Size(max = 255)
    @NotNull
    @Column(name = COLUMN_REPORTCONTENT_NAME, nullable = false)
    private String reportContent;

    @NotNull
    @Column(name = COLUMN_REPORTDATE_NAME, nullable = false)
    private String reportDate;
}