package com.aliens.friendship.domain.market.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MarketArticleComment
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private MarketArticle marketArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

    private MarketArticleComment(
            String content,
            MarketArticle marketArticle,
            MemberEntity member
    ) {
        this.content = content;
        this.marketArticle = marketArticle;
        this.member = member;
    }

    public static MarketArticleComment of(
            String content,
            MarketArticle marketArticle,
            MemberEntity member
    ) {
        return new MarketArticleComment(
                content,
                marketArticle,
                member
        );
    }
}