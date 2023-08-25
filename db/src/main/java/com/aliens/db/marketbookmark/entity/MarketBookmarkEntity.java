package com.aliens.db.marketbookmark.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.marketarticle.entity.MarketArticleEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MarketBookmarkEntity
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private MarketArticleEntity marketArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity memberEntity;

    private MarketBookmarkEntity(
            MarketArticleEntity marketArticle,
            MemberEntity member
    ) {
        this.marketArticle = marketArticle;
        this.memberEntity = member;
    }

    public static MarketBookmarkEntity of(
            MarketArticleEntity marketArticle,
            MemberEntity member
    ) {
        return new MarketBookmarkEntity(
                marketArticle,
                member
        );
    }
}