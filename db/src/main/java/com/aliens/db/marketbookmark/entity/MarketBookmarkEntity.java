package com.aliens.db.marketbookmark.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.packit.packit.domain.member.entity.MemberEntity;
import site.packit.packit.global.audit.BaseEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MarketBookmarkEntity
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private MarketArticle marketArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity memberEntity;

    private MarketBookmarkEntity(
            MarketArticle marketArticle,
            MemberEntity member
    ) {
        this.marketArticle = marketArticle;
        this.memberEntity = member;
    }

    public static MarketBookmarkEntity of(
            MarketArticle marketArticle,
            MemberEntity member
    ) {
        return new MarketBookmarkEntity(
                marketArticle,
                member
        );
    }
}