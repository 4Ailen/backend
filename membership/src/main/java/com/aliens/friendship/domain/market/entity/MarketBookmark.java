package com.aliens.friendship.domain.market.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MarketBookmark
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private MarketArticle marketArticle;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity memberEntity;

    private MarketBookmark(
            MarketArticle marketArticle,
            MemberEntity member
    ) {
        this.marketArticle = marketArticle;
        this.memberEntity = member;
    }

    public static MarketBookmark of(
            MarketArticle marketArticle,
            MemberEntity member
    ) {
        return new MarketBookmark(
                marketArticle,
                member
        );
    }
}