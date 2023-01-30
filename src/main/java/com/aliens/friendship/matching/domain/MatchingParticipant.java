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
@Table(name = MatchingParticipant.TABLE_NAME, schema = "aliendb")
public class MatchingParticipant {
    public static final String TABLE_NAME = "matching_participant";
    public static final String COLUMN_ID_NAME = "matching_participant_id";
    public static final String COLUMN_QUESTIONANSWER_NAME = "question_answer";
    public static final String COLUMN_ISMATCHED_NAME = "is_matched";
    public static final String COLUMN_GROUPID_NAME = "group_id";


    @Id
    @Column(name = COLUMN_ID_NAME, nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matching_participant_id", nullable = false)
    private Member member;

    @Column(name = COLUMN_QUESTIONANSWER_NAME, nullable = false)
    private Integer questionAnswer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "preferred_language", nullable = false)
    private Language preferredLanguage;

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_ISMATCHED_NAME, nullable = false)
    private Status isMatched = Status.NOT_MATCHED;

    @Column(name = COLUMN_GROUPID_NAME, nullable = false)
    private Integer groupId;

    public void setIsMatched(Status isMatched) {
        this.isMatched = isMatched;
    }

    public enum Status {
        MATCHED, NOT_MATCHED;
    }

}