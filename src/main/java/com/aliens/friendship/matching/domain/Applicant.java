package com.aliens.friendship.matching.domain;

import com.aliens.friendship.member.domain.Member;
import lombok.*;

import javax.persistence.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Entity
@Table(name = Applicant.TABLE_NAME, schema = "aliendb")
public class Applicant {
    public static final String TABLE_NAME = "applicant";
    public static final String COLUMN_ID_NAME = "applicant_id";
    public static final String COLUMN_ISMATCHED_NAME = "is_matched";
    public static final String COLUMN_FIRSTPREFERLANGUAGE_NAME = "first_prefer_language";
    public static final String COLUMN_SECONDPREFERLANGUAGE_NAME = "second_prefer_language";

    @Id
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_ID_NAME, nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = COLUMN_ISMATCHED_NAME, nullable = false)
    private Status isMatched = Status.NOT_MATCHED;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_FIRSTPREFERLANGUAGE_NAME, nullable = false)
    private Language firstPreferLanguage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = COLUMN_SECONDPREFERLANGUAGE_NAME, nullable = false)
    private Language secondPreferLanguage;

    public void updateIsMatched(Status isMatched) {
        this.isMatched = isMatched;
    }

    public enum Status {
        MATCHED, NOT_MATCHED;
    }

}