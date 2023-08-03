package com.aliens.db.applicant.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@AllArgsConstructor @NoArgsConstructor
@Getter @ToString @Entity @Table(name = "applicant", schema = "aliendb")
public class ApplicantEntity extends BaseEntity {

    @ManyToOne(optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "member_id",nullable = false)
    private MemberEntity memberEntity;

    @Enumerated(EnumType.STRING)
    @JoinColumn(nullable = false)
    @Builder.Default
    private Status isMatched = Status.NOT_MATCHED;


    @Enumerated(EnumType.STRING)
    @JoinColumn(nullable = false)
    private Language firstPreferLanguage;

    @Enumerated(EnumType.STRING)
    @JoinColumn(nullable = false)
    private Language secondPreferLanguage;


    public void updateIsMatched(Status isMatched) {
        this.isMatched = isMatched;
    }

    public void updatePreferLanguages(Language firstPreferLanguage, Language secondPreferLanguage) {
        this.firstPreferLanguage = firstPreferLanguage;
        this.secondPreferLanguage = secondPreferLanguage;
    }

    public enum Status {
        MATCHED, MATCHING, NOT_MATCHED;
    }
    public enum Language {
        ENGLISH, KOREAN, JAPANESE, CHINESE;
    }

}