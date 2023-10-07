package com.aliens.db.marketarticle.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.marketarticle.MarketArticleStatus;
import com.aliens.db.marketarticle.ProductStatus;
import com.aliens.db.marketarticlecomment.entity.MarketArticleCommentEntity;
import com.aliens.db.marketbookmark.entity.MarketBookmarkEntity;
import com.aliens.db.member.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MarketArticleEntity
        extends BaseEntity {

    @Column(length = 100, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private MarketArticleStatus marketArticleStatus;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ProductStatus productStatus;

    @Column(length = 10000, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

    @OneToMany(mappedBy = "marketArticle", orphanRemoval = true)
    private List<MarketArticleCommentEntity> comments = new ArrayList<>();

    @OneToMany(mappedBy = "marketArticle", orphanRemoval = true)
    private List<MarketBookmarkEntity> likes = new ArrayList<>();

    private MarketArticleEntity(
            String title,
            MarketArticleStatus marketArticleStatus,
            Integer price,
            ProductStatus productStatus,
            String content,
            MemberEntity member
    ) {
        this.title = title;
        this.marketArticleStatus = marketArticleStatus;
        this.price = price;
        this.productStatus = productStatus;
        this.content = content;
        this.member = member;
    }

    public static MarketArticleEntity of(
            String title,
            MarketArticleStatus marketArticleStatus,
            Integer price,
            ProductStatus productStatus,
            String content,
            MemberEntity member
    ) {
        return new MarketArticleEntity(
                title,
                marketArticleStatus,
                price,
                productStatus,
                content,
                member
        );
    }

    public void update(
            String title,
            MarketArticleStatus marketArticleStatus,
            Integer price,
            ProductStatus productStatus,
            String content
    ) {
        this.title = title;
        this.marketArticleStatus = marketArticleStatus;
        this.price = price;
        this.productStatus = productStatus;
        this.content = content;
    }
}