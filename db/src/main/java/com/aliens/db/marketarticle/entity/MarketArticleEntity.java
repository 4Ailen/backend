package com.aliens.db.marketarticle.entity;

import com.aliens.db.BaseEntity;
import com.aliens.db.marketarticle.MarketArticleStatus;
import com.aliens.db.marketarticle.ProductStatus;
import com.aliens.db.member.entity.MemberEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MarketArticleEntity
        extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private MarketArticleStatus status;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private ProductStatus productStatus;

    @Column(length = 10000, nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity member;

    private MarketArticleEntity(
            String title,
            MarketArticleStatus status,
            Integer price,
            ProductStatus productStatus,
            String content,
            MemberEntity member
    ) {
        this.title = title;
        this.status = status;
        this.price = price;
        this.productStatus = productStatus;
        this.content = content;
        this.member = member;
    }

    public static MarketArticleEntity of(
            String title,
            MarketArticleStatus status,
            Integer price,
            ProductStatus productStatus,
            String content,
            MemberEntity member
    ) {
        return new MarketArticleEntity(
                title,
                status,
                price,
                productStatus,
                content,
                member
        );
    }

    public void update(
            String title,
            MarketArticleStatus status,
            Integer price,
            ProductStatus productStatus,
            String content
    ) {
        this.title = title;
        this.status = status;
        this.price = price;
        this.productStatus = productStatus;
        this.content = content;
    }
}